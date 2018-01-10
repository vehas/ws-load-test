(defproject ws-load-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/vehas/ws-load-test"
  :license {:name "MIT"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [http-kit "2.2.0"]
                 [com.taoensso/carmine "2.17.0"]]
  :main ^:skip-aot ws-load-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
