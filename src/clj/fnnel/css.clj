(ns fnnel.css
  (:require
   [garden.def :refer [defstyles]]
   [garden.units :refer [px em percent]]
   [garden.color :as color]))

(defn set-alpha [color a]
  (-> color
      name
      color/hex->rgb
      (assoc :alpha a)))

(def sans-stack
  ["Helvetica Neue" "Helvetica" "sans-serif"])

(defstyles screen
  [:* {:box-sizing "border-box"}]

  [:body
   {:min-width (px 864)
    :font {:size (px 16)
           :family sans-stack}
    :line-height 1.4
    :background {:color :#202026}
    :color :#fff
    :margin 0}]

  [:.container
   {:position "relative"
    :width (px 800)
    :margin [[0 "auto"]]}]

  [:a
   {:text-decoration "none"
    :color :#fff}]

  [:h1 :h2 :h3 :h4
   {:margin 0}]

  [:p
   {:margin [[(em 0.5) 0]]}]

  [:.clearfix
   ["&::before"
    "&::after"
    {:display "table"
     :content "''"}]
   ["&::after"
    {:clear "both"}]]

  [:.right
   {:float "right"}]

  [:.avatar
   {:display "inline-block"
    :border {:radius (px 4)}
    :background {:size "cover"}}]

  [:button
   {:display "inline-block"
    :cursor "pointer"
    :border {:width 0
             :radius (px 3)}
    :background "transparent"
    :font {:family sans-stack
           :size (px 14)}
    :color :#fff
    :padding [[(em 0.5) (em 0.7)]]}

   [:.fa
    {:font {:size (em 1.3)}
     :vertical-align (em -0.1)}
    [:&:first-child {:margin {:right (em 0.3)}}]
    [:&:last-child {:margin {:left (em 0.3)}}]]]

  [:.header
   {:width (percent 100)
    :min-width (px 864)
    :height (px 48)
    :padding [[(px 8) 0]]
    :background :#111
    :overflow "hidden"
    :box-shadow [[0 (px 3) 0 (set-alpha :#202026 0.1)]]}]

  [:.login-button
   {:background {:color :#fff}
    :color :#202026}]

  [:.header-user
   {:display "inline-block"}

   [:.avatar
    {:width (px 24)
     :height (px 24)
     :vertical-align "middle"}]]

  [:.logo
   {:display "inline-block"
    :font {:size (px 24)
           :weight 700}
    :line-height (px 32)
    :color :#95C6ED
    :transition "all 0.2s"}
   [:i
    {:color :#fff
     :margin [[0 (em 0.2)]]
     :transition "all 0.2s"}]

   [:&:hover
    {:color :#fff}
    [:i {:color :#95C6ED}]]]

  )
