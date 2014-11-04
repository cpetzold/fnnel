(ns fnnel.store
  (:require
   [plumbing.core :as p :refer [map-vals] :refer-macros [for-map]]))

(defprotocol IStore
  (initial-state [this])
  (update-state [this state type payload]))

(defn ^boolean store? [x]
  (satisfies? IStore x))

(defn update-children [store-map state type payload]
  (for-map [[k v] store-map]
    k (update-state v (get state k) type payload)))

(deftype Store [handlers init-state store-map]
  IStore
  (initial-state [_]
    (map-vals #(if (store? %) (initial-state %) %) init-state))

  (update-state [_ state type payload]
    (let [h (get handlers type (fn [s _] s))]
      (merge
       (h state payload)
       (update-children store-map state type payload)))))

(defn select-stores [m]
  (->> m
       (filter (fn [[_ v]] (store? v)))
       (into {})))

(defn store
  ([handlers] (partial store handlers))
  ([handlers init-state]
     (let [store-map (select-stores init-state)]
       (Store. handlers init-state store-map))))

(defn dispatch! [state-ref store type payload]
  (swap! state-ref #(update-state store % type payload)))
