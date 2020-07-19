(ns lgdsync.config
  (:require [clojure.java.io :as io]))

(defn- get-config-root-file
  [profile]
  (io/file (System/getenv "HOME") ".config/lgdsync" profile))

(defn get-config-root
  [profile]
  (.getPath (get-config-root-file profile)))

(defn create-config-root
  [profile]
  (let [dir (get-config-root-file profile)]
    (when-not (.exists dir)
      (.mkdir dir))))
