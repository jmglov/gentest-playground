(ns gentest-playground.api
  (:require [com.stuartsierra.component :as component]
            [gentest-playground.apple :as apple]
            [schema.core :refer [Uuid]]
            [yada
             [resource :refer [resource]]
             [yada :as yada]]))

(defonce apples (atom {}))

(def routes
  ["/apples"
   [[""
     (resource
      {:id ::index
       :description "All the apples"
       :produces [{:media-type #{"application/json;q=0.8"}
                   :charset "UTF-8"}]
       :methods
       {:get {:response (fn [ctx]
                          (map apple/->response (apple/sort (vals @apples))))}
        :post {:parameters {:body {:colour String
                                   :grams Integer
                                   :variety String}}
               :consumes [{:media-type #{"application/json"}
                           :charset "UTF-8"}]
               :response (fn [ctx]
                           (let [{:keys [:apple/id] :as apple} (apple/create (get-in ctx [:parameters :body]))]
                             (swap! apples assoc id apple)
                             (java.net.URI. (str "http://localhost:8842/apples/" id))))}}})]
    [["/" :apple]
     (resource
      {:id ::apple
       :description "An apple"
       :parameters {:path {:apple Uuid}}
       :produces [{:media-type #{"application/json;q=0.8"}
                   :charset "UTF-8"}]
       :methods
       {:get {:response (fn [ctx]
                          (let [id (get-in ctx [:parameters :path :apple])]
                            (apple/->response (get @apples id))))}}})]]])

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
