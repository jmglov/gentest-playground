(ns gentest-playground.api-test
  (:require [aleph.http :as http]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.string :as string]
            [clojure.test :as t]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [gentest-playground.api :as api]
            [gentest-playground.apple :as apple])
  (:import (java.util UUID)))

(def base-uri "http://localhost:8842/apples")

(defn clear-apples []
  (reset! api/apples {}))

(s/def :response.apple/id (s/and string? #(try
                                            (UUID/fromString %)
                                            true
                                            (catch Throwable _
                                              false))))

(s/def :response.apple/colour #{"red" "green"})
(s/def :response.apple/variety (s/and string? not-empty))

(s/def :response.weight/unit #{"grams" "ounces"})
(s/def :response.weight/value int?)
(s/def :response.apple/weight (s/keys :req-un [:response.weight/unit
                                               :response.weight/value]))

(s/def :response/apple (s/keys :req-un [:response.apple/id
                                        :response.apple/colour
                                        :response.apple/variety
                                        :response.apple/weight]))

(s/fdef create-and-list
        :args (s/cat :colour (s/and string? not-empty)
                     :grams (s/and pos-int? #(<= % 1000))
                     :variety string?)
        :ret (s/coll-of :response/apple))

(defn create-and-list [colour grams variety]
  @(http/post base-uri {:headers {"Content-Type" "application/json"}
                        :body (json/generate-string {:colour colour
                                                     :grams grams
                                                     :variety variety})})
  (-> @(http/get base-uri)
      :body
      io/reader
      (json/parse-stream true)))

(t/deftest api
  (t/testing "Create and list"
    (-> (stest/check `create-and-list)
        stest/summarize-results)))

(defspec create-and-get
  10
  (prop/for-all
   [colour (s/gen (s/and string? not-empty))
    variety (s/gen string?)
    grams (s/gen (s/and pos-int? #(<= % 1000)))]
   (let [location (-> @(http/post base-uri {:headers {"Content-Type" "application/json"}
                                            :body (json/generate-string {:colour colour
                                                                         :grams grams
                                                                         :variety variety})})
                      (get-in [:headers "Location"]))
         id (-> location (string/split #"/") last)
         expected (-> (apple/create colour variety grams)
                      api/->response
                      (assoc :id id)
                      (update :colour name))
         actual (-> @(http/get location)
                    :body
                    io/reader
                    (json/parse-stream true))]
     (println "Expected:" expected)
     (println "Actual:" actual)
     (= expected actual))))
