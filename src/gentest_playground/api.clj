(ns gentest-playground.api
  (:require [com.stuartsierra.component :as component]
            [gentest-playground.apple :as apple]
            [schema.core :refer [Uuid]]
            [yada
             [resource :refer [resource]]
             [yada :as yada]]))

(defonce apples (atom {}))

(defn ->response [{:keys [:apple/id
                          :apple/colour
                          :apple/variety
                          :apple/weight]}]
  {:id id
   :colour colour
   :variety variety
   :weight {:unit (name (:weight/unit weight))
            :value (:weight/value weight)}})

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
                          (map ->response (apple/sort (vals @apples))))}
        :post {:parameters {:body {:colour String
                                   :grams Integer
                                   :variety String}}
               :consumes [{:media-type #{"application/json"}
                           :charset "UTF-8"}]
               :response (fn [ctx]
                           (let [{:keys [colour variety grams]} (get-in ctx [:parameters :body])
                                 {:keys [:apple/id] :as apple} (apple/create colour variety grams)]
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
                            (->response (get @apples id))))}}})]]])

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
