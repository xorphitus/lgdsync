;; See https://developers.google.com/drive/api/v3/quickstart/java
(ns lgdsync.core
  (:gen-class)
  (:import
   (com.google.api.client.auth.oauth2 Credential)
   (com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp)
   (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver)
   (com.google.api.client.googleapis.auth.oauth2 GoogleAuthorizationCodeFlow$Builder)
   (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets)
   (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
   (com.google.api.client.http.javanet NetHttpTransport)
   (com.google.api.client.json JsonFactory)
   (com.google.api.client.json.jackson2 JacksonFactory)
   (com.google.api.client.util.store FileDataStoreFactory)
   (com.google.api.services.drive Drive)
   (com.google.api.services.drive DriveScopes)
   (com.google.api.services.drive.model File)
   (com.google.api.services.drive.model FileList))
  (:require [clojure.java.io :as io]))

(def ^:private application-name "Google Drive API Java Quickstart")
(def ^:private json-factory (.. JacksonFactory getDefaultInstance))
(def ^:private tokens-directory-path "tokens")
;; Global instance of the scopes required by this quickstart.
;; If modifying these scopes, delete your previously saved tokens/ folder.
(def ^:private scopes [DriveScopes/DRIVE_METADATA_READONLY])
(def ^:private credentials-file-path "/credentials.json")

(defn- get-credentials
  "Creates an authorized Credential object"
  [http-transport]
  (with-open [r (io/reader credentials-file-path)]
    (let [secrets (GoogleClientSecrets/load json-factory r)
          flow (->
                (GoogleAuthorizationCodeFlow$Builder. http-transport json-factory secrets scopes)
                (.setDataStoreFactory (FileDataStoreFactory. (io/file tokens-directory-path)))
                (.setAccessType "offline")
                (.build))])
    ))

(comment
  (get-credentials (GoogleNetHttpTransport/newTrustedTransport))
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
