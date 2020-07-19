(ns lgdsync.googledrive
  (:import
   (com.google.api.client.auth.oauth2 Credential)
   (com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp)
   (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver$Builder)
   (com.google.api.client.googleapis.auth.oauth2 GoogleAuthorizationCodeFlow$Builder)
   (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets)
   (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
   (com.google.api.client.http FileContent)
   (com.google.api.client.http.javanet NetHttpTransport)
   (com.google.api.client.json JsonFactory)
   (com.google.api.client.json.jackson2 JacksonFactory)
   (com.google.api.client.util.store FileDataStoreFactory)
   (com.google.api.services.drive Drive$Builder)
   (com.google.api.services.drive DriveScopes)
   (com.google.api.services.drive.model File)
   (com.google.api.services.drive.model FileList))
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :refer [join]]
            [lgdsync.config :refer [get-config-root]]))

(def ^:private application-name "lgdsync")
(def ^:private json-factory (.. JacksonFactory getDefaultInstance))
(def ^:private scopes [DriveScopes/DRIVE_FILE])
(def ^:private folder-mime-type "application/vnd.google-apps.folder")
(def ^:private callback-url-port 8888)

(defn- get-config-path
  [profile path]
  (-> (get-config-root profile)
      (io/file path)
      (.getPath)))

(defprotocol GoogleCredentialSource
  (exists [this])
  (get-credentials [this])
  (get-token [this])
  (to-location [this]))

(defrecord PlainCredentialSource [profile]
  GoogleCredentialSource
  (exists [this]
    (-> this
        to-location
        io/file
        .exists))
  (get-credentials [this]
    (io/reader
     (to-location this)))
  (get-token [this]
    (-> (get-config-path profile "tokens")
        (io/file)
        (FileDataStoreFactory.)))
  (to-location [this]
    (get-config-path profile "credentials.json")))

(defn- get-auth-info-exec
  "Creates an authorized Credential object"
  [cred-src http-transport]
  (with-open [r (get-credentials cred-src)]
    (let [secrets (GoogleClientSecrets/load json-factory r)
          flow (->
                (GoogleAuthorizationCodeFlow$Builder. http-transport json-factory secrets scopes)
                (.setDataStoreFactory (get-token cred-src))
                (.setAccessType "offline")
                (.build))
          receiver (->
                    (LocalServerReceiver$Builder.)
                    (.setPort callback-url-port)
                    (.build))]
      (.authorize (AuthorizationCodeInstalledApp. flow receiver) "user"))))

(defn- get-auth-info
  [profile http-transport]
  (let [cred-src (PlainCredentialSource. profile)]
    (if (exists cred-src)
      (get-auth-info-exec
       cred-src
       http-transport)
      (throw (Exception. (format "Credential %s is not found" (to-location cred-src)))))))

(s/fdef get-drive-service
  :args (s/cat :profile string?))

(defn get-drive-service
  [profile]
  (let [http-transport (GoogleNetHttpTransport/newTrustedTransport)]
    (->
     (Drive$Builder. http-transport json-factory (get-auth-info profile http-transport))
     (.setApplicationName application-name)
     (.build))))

(defn- to-query-literal
  [v]
  (cond
    (string? v) (format "'%s'" v)
    (coll? v) (format "[%s]" (join \, (map to-query-literal v)))
    :else (str v)))

(s/fdef to-query
  :args (s/cat :q map?))

(defn- to-query
  [q]
  (join
   " and "
   (map #(let [k (key %)
               v (val %)]
           (if (= k "parent")
             (str (to-query-literal v) " in parents")
             (str k \= (to-query-literal v)))) q)))

(s/fdef search-files
  :args (s/cat :drive-service #(not (string? %))
               :fields vector?
               :siz number?
               :q map?))

(defn- search-files
  [drive-service fields siz q]
  (-> drive-service
      (.files)
      (.list)
      (.setPageSize (int siz))
      (.setFields (format "files(%s)" (join \, fields)))
      (.setQ (to-query q))
      (.execute)
      (.getFiles)))

(s/fdef find-sync-dir
  :args (s/cat :drive-service #(not (string? %))
               :name string?))

(defn- find-sync-dir
  [drive-service name]
  (when-let [dir (first
                  (search-files drive-service ["id"] 1
                                {"name" name
                                 "mimeType" folder-mime-type
                                 "trashed" false}))]
    (.getId dir)))

(s/fdef create-sync-dir
  :args (s/cat :drive-service #(not (string? %))
               :name string?))

(defn- create-sync-dir
  [drive-service name]
  (let [metadata (File.)]
    (.setName metadata name)
    (.setMimeType metadata folder-mime-type)
    (-> drive-service
        (.files)
        (.create metadata)
        (.setFields "id")
        (.execute)
        (.getId))))

(s/fdef get-sync-dir
  :args (s/cat :drive-service #(not (string? %))
               :name string?))

(defn get-sync-dir
  [drive-service name]
  (if-let [id (find-sync-dir drive-service name)]
    id
    (create-sync-dir drive-service name)))

(s/fdef get-update-metadata
  :args (s/cat :f #(not (string? %))
               :parent string?))

(defn- get-update-metadata
  ([f]
   (get-update-metadata f nil))
  ([f parent]
   (let [file-metadata (File.)]
     (.setName file-metadata (.getName f))
     (when parent
       (.setParents file-metadata (list parent)))
     file-metadata)))

(defn- create-file
  [drive-service f parent]
  (-> drive-service
      (.files)
      (.create (get-update-metadata f parent) (FileContent. nil f))
      (.execute)))

(defn- update-file
  [drive-service id f]
  (-> drive-service
      (.files)
      (.update id (get-update-metadata f) (FileContent. nil f))
      (.execute)))

(defn- get-same-file
  [drive-service name parent]
  (when-let [file (first (search-files drive-service ["id"] 1
                                       {"name" name
                                        "parent" parent
                                        "trashed" false}))]
    (.getId file)))

(defn- upsert-file
  [drive-service f parent]
  (if-let [id (get-same-file drive-service (.getName f) parent)]
    (update-file drive-service id f)
    (create-file drive-service f parent)))

(defn put-file
  [drive-service f sync-root]
  (when (.exists f)
    (println (str "put files: " (.getAbsolutePath f)))
    (upsert-file drive-service f sync-root)))

