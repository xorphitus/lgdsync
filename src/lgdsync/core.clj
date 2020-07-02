(ns lgdsync.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [lgdsync.googledrive :as gd]))

(def ^:private profile "default")
(def config-root (str (System/getenv "HOME") "/.config/lgdsync/" profile "/"))

(defn- create-config-root
  []
  (let [dir (io/file config-root)]
    (when-not (.exists dir)
      (.mkdir dir))))

(defn -main
  "main"
  [& args]
  (do
    (create-config-root)
    (let [service (gd/get-drive-service)]
      (gd/get-sync-dir service "lgdsync-test"))
    ))
