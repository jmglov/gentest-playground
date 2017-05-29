(ns gentest-playground.apple
  (:refer-clojure :exclude [sort])
  (:import (java.util UUID)))

(defn create [{:keys [colour grams variety]}]
  {:apple/id (UUID/randomUUID)
   :apple/colour colour
   :apple/variety variety
   :apple/weight {:value grams
                  :unit :grams}})

(defn sort [apples]
  apples)

(defn ->response [apple]
  apple)
