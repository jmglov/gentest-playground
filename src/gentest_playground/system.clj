(ns gentest-playground.system
  (:require [com.stuartsierra.component :as component]
            [gentest-playground.api :as api]))

(defn dev-system []
  (component/system-map
   :api (api/create)))

(defn start []
  (try
    (component/start-system (dev-system))
    (catch Exception e
      (println e)
      (System/exit 1))))

(defn stop [system]
  (component/stop-system system))
