(ns build
  "next.jdbc's build script.

  clojure -T:build ci
  clojure -T:build deploy

  Run tests via:
  clojure -X:test

  For more information, run:

  clojure -A:deps -T:build help/doc"
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]))

(def lib 'com.github.seancorfield/next.jdbc)
(defn- the-version [patch] (format "1.3.%s" patch))
(def version (the-version (b/git-count-revs nil)))
(def snapshot (the-version "999-SNAPSHOT"))

(defn test "Run all the tests." [opts]
  (reduce (fn [opts alias]
            (bb/run-tests (assoc opts :aliases [alias])))
          opts
          [:1.10 :1.11 :master])
  opts)

(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (-> opts
      (assoc :lib lib :version (if (:snapshot opts) snapshot version))
      (test)
      (bb/clean)
      (assoc :src-pom "template/pom.xml")
      (bb/jar)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (-> opts
      (assoc :lib lib :version (if (:snapshot opts) snapshot version))
      (bb/deploy)))
