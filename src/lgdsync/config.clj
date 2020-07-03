(ns lgdsync.config
  (:require [clojure.java.io :as io]))

(def profile "default")
(def config-root (str (System/getenv "HOME") "/.config/lgdsync/" profile "/"))

(defn create-config-root
  []
  (let [dir (io/file config-root)]
    (when-not (.exists dir)
      (.mkdir dir))))
