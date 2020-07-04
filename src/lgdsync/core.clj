(ns lgdsync.core
  (:gen-class)
  (:require [clojure.core.async :refer [go-loop <! timeout thread]]
            [clojure.java.io :as io]
            [lgdsync.config :refer [create-config-root]]
            [lgdsync.googledrive :as gd]))

(def ^:private interval 2000)
(def ^:private syncing (atom true))

(defn- now-unix
  []
  (System/currentTimeMillis))

(defn- updated-files
  [path since]
  (->> (io/file path)
       (file-seq)
       (filter #(> (.lastModified %) since))))

(defn- run-file-sync
  [path service sync-root]
  (go-loop [now (now-unix)]
    (when syncing
      (let [fs (updated-files path (- now interval))]
        (thread
          (gd/put-files service fs sync-root)))
      (<! timeout interval)
      (recur (now-unix)))))

(defn- start-file-sync
  [from to]
  (do
    (swap! syncing true)
    (let [service (gd/get-drive-service)]
      (gd/get-sync-dir service to)
      (run-file-sync service from to))))

(defn- stop-file-sync
  []
  (swap! syncing false))

(defn -main
  "main"
  [& args]
  (do
    (create-config-root)
    (start-file-sync (first args) (second args))))
