(ns fnnel.core
  (:require-macros
   [figwheel.client :refer [defonce]]
   [fnnel.macros :refer [defstore defelementk fnk-> fnk->>]])
  (:require
   [clojure.string :as str]
   [plumbing.core :as p :refer-macros [defnk fnk letk]]
   [figwheel.client :as fw :include-macros true]
   [om.core :as om :include-macros true]
   [om-tools.core :refer-macros [defcomponentk]]
   [om-tools.dom :as dom :include-macros true]
   [pani.cljs.core :as pani]

   [fnnel.utils :refer [format]]
   [fnnel.firebase :as firebase]
   [fnnel.store :as store]))

;; --- Globals

(def ref (pani/root "https://fnnel.firebaseio.com"))

;; --- Stores

(defnk github-auth-data->user
  [uid [:github [:cachedUserProfile login name avatar_url]]
   :as auth-data]
  {:uid uid
   :handle login
   :name name
   :avatar avatar_url
   :auth-data auth-data})

(defstore users-store
  :auth (fnk-> [uid :as user] (assoc uid user)))

(defstore client-store
  :auth (fnk-> [uid] (assoc :authed-user-id uid))
  :unauth (fnk-> [] (assoc :authed-user-id nil)))

(defn get-or-create-user [auth-data cb]
  (let [path [:users (:uid auth-data)]]
    (firebase/bind
     ref :value path
     (fnk [val]
       (cb
        (or val
            (let [user (github-auth-data->user auth-data)]
              (pani/set! :users path user)
              user)))))))

;; --- Helpers

(defn update-class [opts class-map]
  (p/update
   opts :class
   #(dom/class-set (merge (when % {% true}) class-map))))

(defelementk icon
  [[:opts type :as opts]]
  (dom/i (update-class
          opts {:fa true
                (str "fa-" (name type)) true})))

(defelementk user-avatar
  [[:opts [:user handle avatar]
    {size :medium}
    {href nil}
    :as opts]]
  (dom/a
    (-> opts
        (assoc :href (or href (str "/" handle))
               :style {:background-image (str "url(" avatar ")")})
        (update-class {:avatar true
                       size true}))))

;; --- Components

(defcomponentk function-head
  [[:data [:function name arglists]]]
  (render [_]
    (dom/div {:class "function-head pagehead sticky"}
      (dom/div {:class "container"}
        (dom/ul {:class "function-arglists"}
                (for [arglist arglists]
                  (dom/li {:class "function-arglist"}
                          "(" (dom/b {:class "function-name"} name)
                          " " (str/join " " arglist) ")")))))))

(defcomponentk function-page
  [[:data users [:function docstring author-id] :as data]]
  (render [_]
    (dom/div {:class "function"}
      (om/build function-head data)
      (dom/div {:class "content container"}
        (when-let [author (get users author-id)]
          (dom/div {:class "function-author text-small"}
            (user-avatar {:user author :size :small})
            (dom/a {:href (str "/" (:handle author))}
              (dom/b (:handle author)))
            " added"))

        (dom/p {:class "function-docstring box"} docstring)))))

(defcomponentk user-nav
  [[:data users [:client authed-user-id]]
   [:shared dispatch!]]
  (render [_]
    (let [authed-user (get users authed-user-id)]
      (dom/div
        {:id "user-nav"}
        (user-avatar {:user authed-user})
        (dom/button
         {:class "icon-button"
          :title "Sign out"
          :on-click #(firebase/unauth ref)}
         (icon {:type :sign-out}))))))

(defcomponentk header
  [[:data [:client authed-user-id] :as data]
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
          "(" (icon {:type :filter}) ")")
        (dom/div
          {:class "right"}
          (if-not authed-user-id
            (dom/button
             {:class "button nav-button"
              :on-click #(firebase/auth-with-oauth-redirect ref :github)}
             (icon {:type :github}) "Login")
            (om/build user-nav data)))))))

(defcomponentk app
  [data [:shared dispatch!]]

  (will-mount [_]
    (firebase/on-auth ref #(get-or-create-user % (partial dispatch! :auth)))
    (firebase/on-unauth ref (partial dispatch! :unauth)))

  (render [_]
    (let [data (om/value data)]
      (dom/div
        (om/build header data)
        (om/build function-page data)))))

;; --- Initialization

(defn init [init-state]
  (let [root-store (store/store nil init-state)
        state (atom (store/initial-state root-store))
        dispatch! (partial store/dispatch! state root-store)
        shared {:dispatch! dispatch!}
        target (.getElementById js/document "app")]
    (om/root app state {:shared shared :target target})
    state))

(init
 {:users (users-store {})
  :client (client-store {:authed-user-id nil})
  :function
  {:author-id "github:96224"
   :name "subs"
   :arglists [["s" "start"] ["s" "start" "end"]]
   :docstring "Returns the substring of s beginning at start inclusive, and ending at end (defaults to length of string), exclusive."
   :implementations [{:author-id "github:96224"}]}})

(fw/watch-and-reload)
