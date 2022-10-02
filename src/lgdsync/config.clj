(ns lgdsync.config
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))

(defn- get-config-root-file
  [profile]
  (io/file (System/getenv "HOME") ".config/lgdsync" profile))

(s/fdef get-config-root
  :args (s/cat :profile string?))

(defn get-config-root
  [profile]
  (.getPath ^java.io.File (get-config-root-file profile)))

(s/fdef create-config-root
  :args (s/cat :profile string?))

(defn create-config-root
  [profile]
  (let [dir ^java.io.File (get-config-root-file profile)]
    (when-not (.exists dir)
      (.mkdir dir))))
