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
            [lgdsync.core :refer [config-root]]))

(def ^:private application-name "Google Drive API Java Quickstart")
(def ^:private json-factory (.. JacksonFactory getDefaultInstance))
(def ^:private scopes [DriveScopes/DRIVE_FILE])
(def ^:private tokens-directory-path (str config-root "tokens"))
(def ^:private credentials-file-path (str config-root "credentials.json"))

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
      (-> (AuthorizationCodeInstalledApp. flow receiver) (.authorize "user")))))

(defn get-drive-service
  ""
  []
  (let [http-transport (GoogleNetHttpTransport/newTrustedTransport)]
    (->
     (Drive$Builder. http-transport json-factory (get-credentials http-transport))
     (.setApplicationName application-name)
     (.build))))

(defn- find-sync-dir
  "Get Google Drive File objects.
  See: https://developers.google.com/resources/api-libraries/documentation/drive/v3/java/latest/com/google/api/services/drive/model/File.html"
  [drive-service name]
  (when-let [dir (->
                  drive-service
                  (.files)
                  (.list)
                  (.setPageSize (int 2))
                  (.setFields "files(id)")
                  (.setQ (str "name = '" name "'"))
                  (.execute)
                  (.getFiles)
                  (first))]
    (.getId dir)))

(defn- create-sync-dir
  ""
  [drive-service name]
  (let [metadata (File.)]
    (do
      (.setName metadata name)
      (.setMimeType metadata "application/vnd.google-apps.folder")
      (let [file (->
                  drive-service
                  (.files)
                  (.create metadata)
                  (.setFields "id")
                  (.execute))]
        (.getId file)))))

(defn get-sync-dir
  ""
  [drive-service name]
  (if-let [id (find-sync-dir drive-service name)]
    id
    (create-sync-dir (get-drive-service) "lgdsync-test")))
