(ns lgdsync.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
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

(s/fdef decrypt
  :args (s/cat :path string?))

(defn decrypt [path]
  (let [{:keys [exit out err]} (sh "gpg" "--decrypt" path)]
    (if (zero? exit)
      out
      (throw (Exception. ^String err)))))
