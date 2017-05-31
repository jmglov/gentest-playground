(ns gentest-playground.apple
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string])
  (:refer-clojure :exclude [sort])
  (:import (java.util UUID)))

(s/def :apple/id uuid?)
(s/def :apple/colour #{:red :green})
(s/def :apple/variety (s/and string? not-empty))

(s/def :weight/unit #{:grams :ounces})
(s/def :weight/value pos-int?)

(s/def :apple/weight (s/keys :req [:weight/unit
                                   :weight/value]))

(s/def :apple/apple (s/keys :req [:apple/id
                                  :apple/colour
                                  :apple/variety
                                  :apple/weight]))

(s/fdef red?
        :args (s/cat :colour string?)
        :ret boolean?)

(defn red? [colour]
  (= "red" (string/lower-case colour)))

(s/fdef create
        :args (s/cat :colour string?
                     :variety string?
                     :grams pos-int?)
        :ret :apple/apple)

(defn create [colour variety grams]
  {:apple/id (UUID/randomUUID)
   :apple/colour (if (red? colour) :red :green)
   :apple/variety (or (not-empty variety) "[unknown]")
   :apple/weight {:weight/unit :grams
                  :weight/value grams}})

(s/fdef weight-in-grams
        :args (s/cat :apple :apple/apple)
        :ret :weight/value)

(defn weight-in-grams [{:keys [:apple/weight]}]
  (let [{:keys [:weight/unit :weight/value]} weight]
    (if (= :grams unit)
      value
      (int (* value 28.3495)))))

(s/fdef sort
        :args (s/cat :apples (s/coll-of :apple/apple))
        :ret (s/coll-of :apple/apple)
        :fn #(and (= (count (-> % :args :apples))
                     (count (-> % :ret)))
                  (every? (fn [[a b]] (<= (weight-in-grams a) (weight-in-grams b)))
                          (partition 2 1 (-> % :args :apples)))))

(defn sort [apples]
  (sort-by weight-in-grams apples))
