(ns lgdsync.core
  (:gen-class)
  (:require [clojure.core.async :refer [go-loop <! <!! timeout thread chan]]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.tools.cli :refer [parse-opts]]
            [lgdsync.config :refer [create-config-root]]
            [lgdsync.googledrive :as gd]))

(def ^:private interval 2000)
(def ^:private syncing (atom true))
(def ^:private default-profile "default")

(defn- now-unix
  []
  (System/currentTimeMillis))

(s/fdef updated-files
  :args (s/cat :path string?
               :since number?))

(defn- updated-files
  [path since]
  (->> (io/file path)
       (file-seq)
       (filter #(and
                 (> (.lastModified ^java.io.File %) since)
                 (.isFile ^java.io.File %)))))

(s/fdef run-file-sync
  :args (s/cat :service #(not (string? %))
               :path string?
               :sync-root string?))

(defn- run-file-sync
  [service path sync-root]
  ;; Currently it's no use using go-loop.
  (go-loop [now (now-unix)]
    (when @syncing
      (let [fs (updated-files path (- now interval))]
        (doseq [f fs]
          (thread
            (gd/put-file service f sync-root))))
      (<! (timeout interval))
      (recur (now-unix)))))

(s/fdef start-file-sync
  :args (s/cat :profile string?
               :src string?
               :dest string?))

(defn- start-file-sync
  [profile src dest]
  (do
    (reset! syncing true)
    (if-let [service (try
                       (gd/get-drive-service profile)
                       (catch Exception e
                         (println "Invalid credential")
                         (println (.getMessage e))
                         nil))]
      (let [dest-on-cloud (gd/get-sync-dir service dest)]
        (run-file-sync service src dest-on-cloud))
      ;; workaround
      (chan nil))))

(defn- stop-file-sync
  []
  (reset! syncing false))

(def cli-options
  ^:private
  [["-p" "--profile NAME" "Profile name"
    :default default-profile
    :validate [not-empty "Must specify a profile name"]]
   ["-h" "--help"]])

(defn- usage [options-summary]
  (string/join
   \newline
   ["Usage: lgdsync [options] /path/to/source/dir /path/to/destination/dir"
    ""
    "Options:"
    options-summary]))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn- validate-args
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}
      errors
      {:exit-message (error-msg errors)}
      (= 2 (count arguments))
      {:src (first arguments)
       :dest (second arguments)
       :options options}
      :else
      {:exit-message (usage summary)})))

(comment
  (st/unstrument)
  (st/instrument)
  (updated-files (str (System/getenv "HOME") "/tmp") (- (now-unix) (* 1000 60 60)))
  (start-file-sync "default" (str (System/getenv "HOME") "/tmp") "lgdsync-test")
  (stop-file-sync)
  (comment))

(defn -main
  "main"
  [& args]
  (let [{:keys [src dest options exit-message ok?]} (validate-args args)
        profile (:profile options)]
    (if exit-message
      (do
        (println exit-message)
        (System/exit (if ok? 0 1)))
      (do
        (println (str "start lgdsync with profile " profile ", from " src " to " dest))
        (create-config-root profile)
        (<!!
         (start-file-sync profile src dest))))))
