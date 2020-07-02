(ns lgdsync.core
  (:gen-class)
  (:require [clojure.core.async :refer [go go-loop <! >! chan alt! timeout]]
            [clojure.java.io :as io]
            [lgdsync.googledrive :as gd]))

(def ^:private profile "default")
(def config-root (str (System/getenv "HOME") "/.config/lgdsync/" profile "/"))
(def ^:private closer (chan))

(defn- create-config-root
  []
  (let [dir (io/file config-root)]
    (when-not (.exists dir)
      (.mkdir dir))))

(defn now-unix
  []
  (System/currentTimeMillis))

(defn run-file-sync
  [path]
  (let [ticker (timeout 1000)]
    (go-loop [now (now-unix)]
      (alt!
        ticker
        (do
          (->> (io/file path)
               (file-seq)
               (filter #(> (.lastModified %) now)))
          (recur (now-unix)))
        closer
        (println "stop sync")))))

(defn stop-file-sync
  []
  (go (>! closer true)))

(comment
  (run-file-sync (System/getenv "HOME"))
  (stop-file-sync)
  )

(defn -main
  "main"
  [& args]
  (do
    (create-config-root)
    (let [service (gd/get-drive-service)]
      (gd/get-sync-dir service "lgdsync-test"))
    ))
