(defproject lgdsync "0.2.0"
  :description "Lite Google Drive Sync"
  :url "https://github.com/xorphitus/lgdsync"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/core.async "1.5.648"]
                 [org.clojure/tools.cli "1.0.206"]
                 [com.google.api-client/google-api-client "1.35.2"]
                 [com.google.oauth-client/google-oauth-client-jetty "1.23.0"]
                 [com.google.apis/google-api-services-drive "v3-rev197-1.25.0"]]
  :plugins [[lein-cljfmt "0.9.0"]
            [lein-kibit "0.1.8"]
            [jonase/eastwood "1.3.0"]]
  :main ^:skip-aot lgdsync.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
