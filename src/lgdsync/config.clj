(ns lgdsync.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))

(def ^:private property
  (when-let [conf (io/resource "property.edn")]
    (-> conf
        (slurp)
        (edn/read-string))))

(s/fdef get-property
  :args (s/cat :k keyword?))

(defn get-property
  "Get application properties."
  [k]
  (k property))

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
