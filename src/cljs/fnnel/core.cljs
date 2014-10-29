(ns fnnel.core
  (:require-macros
   [figwheel.client :refer [defonce]])
  (:require
   [plumbing.core :refer-macros [defnk]]
   [figwheel.client :as fw :include-macros true]
   [om.core :as om :include-macros true]
   [om-tools.core :refer-macros [defcomponentk]]
   [om-tools.dom :as dom :include-macros true]
   [pani.cljs.core :as pani]

   [fnnel.firebase :as firebase]
   [fnnel.dispatch :as dispatch]))

(def ref (pani/root "https://fnnel.firebaseio.com"))

(defnk github-auth-data->user
  [uid [:github [:cachedUserProfile login name avatar_url]]
   :as auth-data]
  {:uid uid
   :handle login
   :name name
   :avatar avatar_url
   :auth-data auth-data})

(defmethod dispatch/dispatch! :authed [state type data]
  (js/console.log (clj->js data))
  (swap! state assoc
         :authing? false
         :user (github-auth-data->user data)))

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

(defn icon [type]
  (dom/i {:class (str "fa fa-" (name type))}))

(defn login-button [on-click]
  (dom/button
   {:class "login-button"
    :on-click on-click}
   (icon :github-alt)
   "Login"))

(defcomponentk header-user
  [[:data [:user handle avatar]]]
  (render [_]
    (dom/div
     {:class "header-user"}
     (dom/a
      {:class "avatar"
       :href (str "/profile/" handle)
       :style {:background-image (str "url(" avatar ")")}}))))

(defcomponentk header
  [[:data {user nil} :as data]
   [:shared dispatch!]]
  (render [_]
    (dom/div
     {:class "header"}
     (dom/div
      {:class "container clearfix"}
      (dom/a
       {:class "logo"
        :title "fnnel"
        :href "/"}
       "(" (icon :filter) ")")

      (dom/div
       {:class "right"}
       (if-not user
         (login-button #(dispatch! :auth))
         (dom/div
          (om/build header-user data)
          (dom/button
           {:on-click #(dispatch! :unauth)}
           "Logout"))))))))

(defcomponentk app
  [data [:shared dispatch!]]
  (will-mount [_]
    (firebase/on-auth ref (partial dispatch! :authed))
    (firebase/on-unauth ref (partial dispatch! :unauthed)))

  (render [_]
    (om/build header data)))

(defn ^:export init [init-state]
  (let [state (atom init-state)]
    (om/root
     app state
     {:target (.getElementById js/document "app")
      :shared {:dispatch! (partial dispatch/dispatch! state)}})))

(init {})

(fw/watch-and-reload)
