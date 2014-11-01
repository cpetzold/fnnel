(ns fnnel.macros
  (:use plumbing.core))

(defmacro fnk-> [bindings & body]
  `(fn [x# y#]
     (letk [~bindings y#]
       (-> x# ~@body))))

(defmacro fnk->> [bindings & body]
  `(fn [x# y#]
     (letk [~bindings y#]
       (->> x# ~@body))))

(defmacro defstore [name & {:as handlers}]
  `(def ~name (fnnel.store/store ~handlers)))
