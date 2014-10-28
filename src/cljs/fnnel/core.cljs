(ns fnnel.core
  (:require-macros
   [figwheel.client :refer [defonce]])
  (:require
   [figwheel.client :as fw :include-macros true]
   [om.core :as om :include-macros true]
   [om-tools.core :refer-macros [defcomponentk]]
   [om-tools.dom :as dom :include-macros true]
   [pani.cljs.core :as pani]

   [fnnel.firebase :as firebase]
   [fnnel.dispatch :as dispatch]))

(def ref (pani/root "https://fnnel.firebaseio.com"))

(defmethod dispatch/dispatch! :authed [state type data]
  (swap! state assoc
         :authing? false
         :user data))

(defmethod dispatch/dispatch! :unauthed [state type data]
  (swap! state assoc
         :unauthing? false
         :user nil))

(defmethod dispatch/dispatch! :auth [state type data]
  (swap! state assoc :authing? true)
  (firebase/auth-with-oauth-popup ref :github))

(defmethod dispatch/dispatch! :unauth [state type data]
  (swap! state assoc :unauthing? true)
  (firebase/unauth ref))

(defcomponentk app
  [[:data {init nil} {user nil} :as data] [:shared dispatch!]]
  (will-mount [_]
    (firebase/on-auth ref (partial dispatch! :authed))
    (firebase/on-unauth ref (partial dispatch! :unauthed)))

  (render [_]
    (if user
      (dom/div
       (get-in user [:github :displayName])
       (dom/button
        {:on-click #(dispatch! :unauth)}
        "Logout"))
      (dom/button
       {:on-click #(dispatch! :auth)}
       "Login with GitHub"))))

(defn init [init-state]
  (let [state (atom init-state)]
    (om/root
     app state
     {:target (.getElementById js/document "app")
      :shared {:dispatch! (partial dispatch/dispatch! state)}})))

(init {})

(fw/watch-and-reload)
