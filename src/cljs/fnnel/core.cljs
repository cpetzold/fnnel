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
  (swap! state assoc :user (github-auth-data->user data)))

(defmethod dispatch/dispatch! :unauthed [state type data]
  (swap! state assoc
         :unauthing? false
         :user nil))

(defmethod dispatch/dispatch! :auth [state type data]
  (firebase/auth-with-oauth-redirect ref :github))

(defmethod dispatch/dispatch! :unauth [state type data]
  (swap! state assoc :unauthing? true)
  (firebase/unauth ref))

(defn icon [type]
  (dom/i {:class (str "fa fa-" (name type))}))

(defcomponentk user-nav
  [[:data [:user handle avatar]]
   [:shared dispatch!]]
  (render [_]
    (dom/div
     {:id "user-nav"}
     (dom/a
      {:class "avatar"
       :href (str "/profile/" handle)
       :style {:background-image (str "url(" avatar ")")}})
     (dom/button
      {:class "icon-button"
       :title "Sign out"
       :on-click #(dispatch! :unauth)}
      (icon :sign-out)))))

(defcomponentk header
  [[:data {user nil} :as data]
   [:shared dispatch!]]
  (render [_]
    (dom/div
     {:id "header"}
     (dom/div
      {:class "container clearfix"}
      (dom/a
       {:id "logo"
        :title "fnnel"
        :href "/"}
       "(" (icon :filter) ")")

      (dom/div
       {:class "right"}
       (if-not user
         (dom/button
          {:class "button nav-button"
           :on-click #(dispatch! :auth)}
          (icon :github) "Login")
         (om/build user-nav data)))))))

(defcomponentk app
  [data [:shared dispatch!]]
  (will-mount [_]
    (firebase/on-auth ref (partial dispatch! :authed))
    (firebase/on-unauth ref (partial dispatch! :unauthed)))

  (render [_]
    (let [data (om/value data)]
      (dom/div
       (om/build header data)
       (dom/div {:id "content"})))))

(defn ^:export init [init-state]
  (let [state (atom init-state)]
    (om/root
     app state
     {:target (.getElementById js/document "app")
      :shared {:dispatch! (partial dispatch/dispatch! state)}})))

(init {})

(fw/watch-and-reload)
