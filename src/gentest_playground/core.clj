(ns gentest-playground.core
  (:require [gentest-playground.system :as system])
  (:gen-class))

(defn -main [& _]
  (system/start)
  ;; Sleeping to prevent the service from exiting when only running the API
  (while true (Thread/sleep 1000)))
