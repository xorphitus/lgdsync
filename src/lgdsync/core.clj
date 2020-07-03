(ns lgdsync.core
  (:gen-class)
  (:require [clojure.core.async :refer [go go-loop <! >! chan alt! timeout]]
            [clojure.java.io :as io]
            [lgdsync.config :refer [create-config-root]]
            [lgdsync.googledrive :as gd]))

(def ^:private closer (chan))

(defn- now-unix
  []
  (System/currentTimeMillis))

(defn run-file-sync
  [path]
  (let [ticker (timeout 1000)]
    (go-loop [now (now-unix)]
      (alt!
        ticker
        (do
          (println "yes")
          (->> (io/file path)
               (file-seq)
               (filter #(> (.lastModified %) now))
               (gd/put-files))
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
