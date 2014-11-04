(ns fnnel.core
  (:require-macros
   [figwheel.client :refer [defonce]]
   [fnnel.macros :refer [defstore fnk-> fnk->>]])
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

;; --- Helpers

(defnk github-auth-data->user
  [uid [:github [:cachedUserProfile login name avatar_url]]
   :as auth-data]
  {:uid uid
   :handle login
   :name name
   :avatar avatar_url
   :auth-data auth-data})

(defn icon [type]
  (dom/i {:class (str "fa fa-" (name type))}))

(defn arglist->str [name arglist]
  (format "(%s)" (str/join " "(cons name arglist))))

;; --- Stores

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

;; --- Components

(defcomponentk function-page
  [[:data [:function name arglists docstring]]]
  (render [_]
    (dom/div
     {:class "function container"}
     (dom/h2 name)
     (dom/ul
      {:class "function-arglists"}
      (for [arglist arglists]
        (dom/li
         {:class "function-arglist"}
         (arglist->str name arglist))))
     (dom/p
      {:class "function-docstring"}
      docstring))))

(defcomponentk user-nav
  [[:data users [:client authed-user-id]]
   [:shared dispatch!]]
  (render [_]
    (p/letk [[handle avatar] (get users authed-user-id)]
      (dom/div
       {:id "user-nav"}
       (dom/a
        {:class "avatar"
         :href (str "/profile/" handle)
         :style {:background-image (str "url(" avatar ")")}})
       (dom/button
        {:class "icon-button"
         :title "Sign out"
         :on-click #(firebase/unauth ref)}
        (icon :sign-out))))))

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
       "(" (icon :filter) ")")

      (dom/div
       {:class "right"}
       (if-not authed-user-id
         (dom/button
          {:class "button nav-button"
           :on-click #(firebase/auth-with-oauth-redirect ref :github)}
          (icon :github) "Login")
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
       (dom/div
        {:id "content"}
        (om/build function-page data))))))

;; --- Initialization

(defn root [root-store]
  (let [state (atom (store/initial-state root-store))
        dispatch! (partial store/dispatch! state root-store)
        shared {:dispatch! dispatch!}
        target (.getElementById js/document "app")]
    (om/root app state {:shared shared :target target})
    (fn [_] (clj->js @state))))

(def root-store (store/store {}))

(def ^:export current-state
  (root
   (root-store
    {:users (users-store {})
     :client (client-store {:authed-user-id nil})
     :function
     {:name "pwn"
      :arglists [["s" "start"] ["s" "start" "end"]]
      :docstring "Returns the substring of s beginning at start inclusive, and ending at end (defaults to length of string), exclusive."
      :implementations [{:author "github:96224"}]}})))

(fw/watch-and-reload)
