(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'lgdsync)
(def version (b/git-process {:git-args "rev-parse --short HEAD"}))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(defn uber-file [version]
  (format "target/%s-%s-standalone.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [arg]
  "You can build a uberjar file by a command like the following examples.
  If you pass the version argument, it will be applied to the build name.
  If you ommit the version argument, a commit hash will be used instead.

  Examples:
    $ clojure -T:build uber '{:version \"v0.1.0\"}'
    $ clojure -T:build uber

  And a build archifact file name will appear in stdout."
  (clean nil)
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (let [ver (or (:version arg) version)
        uber-name (uber-file ver)]
    (b/uber {:class-dir class-dir
             :uber-file uber-name
             :basis basis
             :main 'lgdsync.core})
    (println uber-name)))
