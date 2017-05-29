(ns gentest-playground.api
  (:require [com.stuartsierra.component :as component]
            [gentest-playground.apple :as apple]
            [schema.core :refer [Keyword]]
            [yada
             [resource :refer [resource]]
             [yada :as yada]]))

(def apples (atom {}))

(def routes
  ["/apples"
   [["" (resource
         {:description "All the apples"
          :produces [{:media-type #{"application/json;q=0.8"}
                      :charset "UTF-8"}]
          :methods
          {:get {:response (fn [ctx]
                             (map apple/->response (apple/sort (vals @apples))))}}})]]])

(defrecord Api [config]
  component/Lifecycle
  (start [component]
    (assoc component
           :server
           (yada/listener routes
                          {:port 8842})))
  (stop [component]
    (when-let [close (some-> component :server :close)]
      (close))
    (assoc component :server nil)))

(defn create []
  (->Api {}))
