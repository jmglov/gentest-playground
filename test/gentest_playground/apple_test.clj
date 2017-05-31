(ns gentest-playground.apple-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :as t]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [gentest-playground.apple :as apple]))

(defn get-unit [apple]
  (get-in apple [:apple/weight :weight/unit]))

(defn get-weight [apple]
  (get-in apple [:apple/weight :weight/value]))

(t/deftest spec
  ;; this won't work
  #_(t/testing "weight-in-grams"
      (->> (stest/check `apple/weight-in-grams) stest/summarize-results))
  (t/testing "create"
    (->> (stest/check `apple/create) stest/summarize-results))
  (t/testing "sort"
    (->> (stest/check `apple/sort) stest/summarize-results)))

(defn constrain-weight [apple]
  (let [max-val (case (get-unit apple)
                  :grams 1000
                  :ounces 35)]
    (update-in apple [:apple/weight :weight/value] min max-val)))

(defspec weight-in-grams-tc
  100
  (prop/for-all
   [apple (gen/fmap constrain-weight (s/gen :apple/apple))]
   (let [unit (get-unit apple)
         weight (get-weight apple)
         weight-in-grams (apple/weight-in-grams apple)]
     (case unit
       :grams (= weight weight-in-grams)
       :ounces (< weight weight-in-grams)))))

(defspec red?
  100
  (let [reds #{"red" "Red" "rED" "ReD" "reD" "rEd"}]
    (prop/for-all
     [colour (s/gen (s/or :red reds
                          :string string?))]
     (if (contains? reds colour)
       (apple/red? colour)
       (not (apple/red? colour))))))

(defspec sort-tc
  100
  (prop/for-all
   [apples (gen/fmap #(map constrain-weight %) (s/gen (s/coll-of :apple/apple)))]
   (every? (fn [[a b]] (<= (apple/weight-in-grams a) (apple/weight-in-grams b)))
           (partition 2 1 (apple/sort apples)))))
