(defproject lgdsync "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "1.2.603"]
                 [com.google.api-client/google-api-client "1.23.0"]
                 [com.google.oauth-client/google-oauth-client-jetty "1.23.0"]
                 [com.google.apis/google-api-services-drive "v3-rev110-1.23.0"]]
  :plugins [[lein-cljfmt "0.6.8"]
            [lein-kibit "0.1.8"]
            [jonase/eastwood "0.3.10"]]
  :main ^:skip-aot lgdsync.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
