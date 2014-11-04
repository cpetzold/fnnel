(ns fnnel.firebase
  (:require
   [pani.cljs.core :as pani]))

(defn on-auth* [ref cb]
  (.onAuth ref #(cb (js->clj % :keywordize-keys true))))

(defn on-auth [ref cb]
  (on-auth* ref #(when % (cb %))))

(defn on-unauth [ref cb]
  (on-auth* ref #(when-not % (cb))))

(defn auth-with-oauth-redirect [ref service & [cb]]
  (.authWithOAuthRedirect
   ref (name service)
   (fn [err]
     (when (and cb err) (cb (js->clj err :keywordize-keys true))))))

(defn unauth [ref]
  (.unauth ref))

(defn bind
  "Bind to a certain property under the given root"
  [root type korks cb]
  (let [c (pani/walk-root root korks)]
    (.on c (name type)
         #(when-let [v (pani/clj-val %1)]
            (cb {:val v :name (pani/name %1)})))))
