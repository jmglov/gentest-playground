(ns gentest-playground.apple
  (:refer-clojure :exclude [sort])
  (:import (java.util UUID)))

(defn create [colour variety grams]
  {:apple/id (UUID/randomUUID)
   :apple/colour (keyword colour)
   :apple/variety (or (not-empty variety) "[unknown]")
   :apple/weight {:weight/unit :grams
                  :weight/value grams}})

(defn weight-in-grams [{:keys [:apple/weight]}]
  (let [{:keys [:weight/unit :weight/value]} weight]
    (if (= :grams unit)
      value
      (* value 28.3495))))

(defn sort [apples]
  apples)
