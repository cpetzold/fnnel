(ns fnnel.macros
  (:use plumbing.core)
  (:require
   [om-tools.util :as util]))

(defmacro defelementk
  {:arglists '([name doc-string? attr-map? [bindings*] body])}
  [name & args]
  (let [[doc-string? args] (util/maybe-split-first string? args)
        [attr-map? args] (util/maybe-split-first map? args)
        [arglist & args] args
        [prepost-map? body] (util/maybe-split-first map? args)]
    `(defn ~name
       ~@(when doc-string? [doc-string?])
       ~@(when attr-map? [attr-map?])
       [opts# & children#]
       (let [[opts# children#] (fnnel.utils/element-args opts# children#)]
         (p/letk [~arglist {:opts opts# :children (flatten children#)}]
           ~@body)))))

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
