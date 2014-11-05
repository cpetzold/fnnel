(ns fnnel.utils
  (:require
   [goog.string :as gstring]
   [goog.string.format]))

(defn format
  "Formats a string using goog.string.format."
  [fmt & args]
  (apply gstring/format fmt args))

(defn element-args [opts children]
  (cond
   (nil? opts) [nil children]
   (map? opts) [opts children]
   :else [nil (cons opts children)]))
