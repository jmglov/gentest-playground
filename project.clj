(defproject gentest-playground "0.1-SNAPSHOT"
  :description "Playing around with generative testing"
  :url ""
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.async "0.3.443"]
                 [cheshire "5.7.1"]
                 [com.stuartsierra/component "0.3.2"]

                 ;; Yada
                 [aleph "0.4.3"]
                 [yada "1.2.2" :exclusions [aleph
                                            org.clojure/core.async]]]
  :main gentest-playground.core
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.clojure/test.check "0.9.0"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [pjstadig/humane-test-output "0.8.1"]
                                  [reloaded.repl "0.2.3"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}
                   :monkeypatch-clojure-test false  ; test.check will break without this
                   :aliases {"lint" ["do"
                                     ["clean"]
                                     ["bikeshed"]
                                     ["cljfmt" "check"]]
                             "omni" ["do"
                                     ["lint"]
                                     ["ancient"]]}}}

  ;; Run only integration tests with "lein test :integration", only unit tests
  ;; with "lein test", and both with "lein test :all".
  ;; See http://stackoverflow.com/a/23017734/58994
  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)})
