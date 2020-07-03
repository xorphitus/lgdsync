(ns lgdsync.core
  (:gen-class)
  (:require [clojure.core.async :refer [go go-loop <! >! chan alt! timeout]]
            [clojure.java.io :as io]
            [lgdsync.config :refer [create-config-root]]
            [lgdsync.googledrive :as gd]))

(def ^:private interval 2000)
(def ^:private close (chan))

(defn- now-unix
  []
  (System/currentTimeMillis))

(defn- updated-files
  [path since]
  (->> (io/file path)
       (file-seq)
       (filter #(> (.lastModified %) since))))

(defn run-file-sync
  [path service]
  (let [ticker (timeout interval)]
    (go-loop [now (now-unix)]
      (alt!
        ticker
        (do
          (gd/put-files
           service
           (updated-files path (- now interval)))
          (recur (now-unix)))
        close
        (comment "do nothing")))))

(defn- stop-file-sync
  []
  (go (>! close true)))

(comment
  (stop-file-sync)
  )

(defn -main
  "main"
  [& args]
  (do
    (create-config-root)
    (let [from (first args)
          to (second args)
          service (gd/get-drive-service)]
      (gd/get-sync-dir service to)
      (run-file-sync service from))))
