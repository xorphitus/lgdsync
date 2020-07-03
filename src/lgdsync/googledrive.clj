(ns lgdsync.googledrive
  (:import
   (com.google.api.client.auth.oauth2 Credential)
   (com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp)
   (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver$Builder)
   (com.google.api.client.googleapis.auth.oauth2 GoogleAuthorizationCodeFlow$Builder)
   (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets)
   (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
   (com.google.api.client.http.javanet NetHttpTransport)
   (com.google.api.client.json JsonFactory)
   (com.google.api.client.json.jackson2 JacksonFactory)
   (com.google.api.client.util.store FileDataStoreFactory)
   (com.google.api.services.drive Drive$Builder)
   (com.google.api.services.drive DriveScopes)
   (com.google.api.services.drive.model File)
   (com.google.api.services.drive.model FileList))
  (:require [clojure.java.io :as io]
            [clojure.string :refer [join]]
            [lgdsync.config :refer [config-root]]))

(def ^:private application-name "Google Drive API Java Quickstart")
(def ^:private json-factory (.. JacksonFactory getDefaultInstance))
(def ^:private scopes [DriveScopes/DRIVE_FILE])
(def ^:private tokens-directory-path (str config-root "tokens"))
(def ^:private credentials-file-path (str config-root "credentials.json"))
(def ^:private folder-mime-type "application/vnd.google-apps.folder")

(defn- get-credentials
  "Creates an authorized Credential object"
  [http-transport]
  (with-open [r (io/reader credentials-file-path)]
    (let [secrets (GoogleClientSecrets/load json-factory r)
          flow (->
                (GoogleAuthorizationCodeFlow$Builder. http-transport json-factory secrets scopes)
                (.setDataStoreFactory (FileDataStoreFactory. (io/file tokens-directory-path)))
                (.setAccessType "offline")
                (.build))
          receiver (->
                    (LocalServerReceiver$Builder.)
                    (.setPort 8888)
                    (.build))]
      (.authorize (AuthorizationCodeInstalledApp. flow receiver) "user"))))

(defn get-drive-service
  ""
  []
  (let [http-transport (GoogleNetHttpTransport/newTrustedTransport)]
    (->
     (Drive$Builder. http-transport json-factory (get-credentials http-transport))
     (.setApplicationName application-name)
     (.build))))

(defn- list-sync-dir
  [drive-service fields siz name mime-type]
  (-> drive-service
      (.files)
      (.list)
      (.setPageSize (int siz))
      (.setFields (str "files(" (join \, fields) ")"))
      (.setQ (str "name='" name "' and mimeType='" mime-type "' and trashed=false"))
      (.execute)
      (.getFiles)))

(join \, ["a" "b"])

(defn- find-sync-dir
  [drive-service name]
  (when-let [dir (first
                  (list-sync-dir drive-service ["id"] 1 name folder-mime-type))]
    (.getId dir)))

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

(defn get-sync-dir
  [drive-service name]
  (if-let [id (find-sync-dir drive-service name)]
    id
    (create-sync-dir (get-drive-service) name)))

(defn- put-file
  [drive-service f]
  (print (.getName f)))

(defn put-files
  [drive-service fs]
  (doseq [f fs] (put-file drive-service f)))
