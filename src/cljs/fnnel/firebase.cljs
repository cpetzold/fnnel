(ns fnnel.firebase)

(defn on-auth* [ref cb]
  (.onAuth ref #(cb (js->clj % :keywordize-keys true))))

(defn on-auth [ref cb]
  (on-auth* ref #(when % (cb %))))

(defn on-unauth [ref cb]
  (on-auth* ref #(when-not % (cb))))

(defn auth-with-oauth-popup [ref service & [cb]]
  (.authWithOAuthPopup
   ref (name service)
   (fn [err auth-data]
     (when cb
       (cb (js->clj err :keywordize-keys true)
           (js->clj auth-data :keywordize-keys true))))))

(defn unauth [ref]
  (.unauth ref))
