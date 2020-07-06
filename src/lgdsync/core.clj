(ns lgdsync.core
  (:gen-class)
  (:require [clojure.core.async :refer [go-loop <! <!! timeout thread]]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [lgdsync.config :refer [create-config-root]]
            [lgdsync.googledrive :as gd]))

(def ^:private interval 2000)
(def ^:private syncing (atom true))

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
                 (> (.lastModified %) since)
                 (.isFile %)))))

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
        (thread
          (gd/put-files service fs sync-root)))
      (<! (timeout interval))
      (recur (now-unix)))))

(s/fdef start-file-sync
  :args (s/cat :from string?
               :to string?))

(defn- start-file-sync
  [from to]
  (do
    (reset! syncing true)
    (let [service (gd/get-drive-service)
          to-on-cloud (gd/get-sync-dir service to)]
      (run-file-sync service from to-on-cloud))))

(defn- stop-file-sync
  []
  (reset! syncing false))

(comment
  (st/unstrument)
  (st/instrument)
  (updated-files (str (System/getenv "HOME") "/tmp") (- (now-unix) (* 1000 60 60)))
  (start-file-sync (str (System/getenv "HOME") "/tmp") "lgdsync-test")
  (stop-file-sync))

(defn -main
  "main"
  [& args]
  (do
    (println "start lgdsync")
    (create-config-root)
    (<!!
     (start-file-sync (first args) (second args)))))
