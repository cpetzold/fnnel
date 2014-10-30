(ns fnnel.css
  (:require
   [garden.def :refer [defstyles defcssfn]]
   [garden.units :refer [px em percent]]
   [garden.color :as color]))

(defn set-alpha [color a]
  (-> color
      name
      color/hex->rgb
      (assoc :alpha a)))

(def sans-stack
  ["Helvetica Neue" "Helvetica" "sans-serif"])

(def header-height (px 48))

(defcssfn -webkit-linear-gradient)

(defstyles screen
  [:* {:box-sizing "border-box"}]

  [:html
   {:padding {:top header-height}}]

  [:body
   {:min-width (px 864)
    :font {:size (px 16)
           :family sans-stack}
    :line-height 1.4
    :background {:color :#111}
    :color :#111
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
    :background "transparent"
    :font {:family sans-stack
           :size (px 13)
           :weight 600}
    :line-height 1
    :color :#fff
    :border {:width 0
             :radius (px 3)}
    :padding [[(px 8) (px 12)]]
    :margin 0}

   [:.fa {:font {:size (px 16)}}]]

  [:.button
   [:.fa
    {:vertical-align (px -1)}
    [:&:first-child {:margin {:right (em 0.3)}}]
    [:&:last-child {:margin {:left (em 0.3)}}]]]

  [:#content
   {:position "relative"
    :background :#fff
    :width (percent 100)
    :height (px 5000)}]

  [:#header
   {:position "fixed"
    :top 0
    :z-index -1
    :width (percent 100)
    :min-width (px 864)
    :min-height header-height
    :padding [[(px 8) 0]]
    :overflow "hidden"
    :color :#fff}]

  [:.nav-button
   {:background (-webkit-linear-gradient
                 :top
                 (color/lighten "#111" 8)
                 (color/lighten "#111" 4))
    :color :#eee}

   [:.fa {:color :#aaa}]

   [:&:hover
    {:background (-webkit-linear-gradient
                  :top
                  (color/lighten "#111" 10)
                  (color/lighten "#111" 6))
     :color :#fff}
    [:.fa {:color :#ddd}]]

   [:&:active
    :&.selected
    {:background (-webkit-linear-gradient
                  :bottom
                  (color/lighten "#111" 10)
                  (color/lighten "#111" 6))
     :color :#eee}]]

  [:#user-nav
   {:display "inline-block"}

   [:.avatar
    {:width (px 24)
     :height (px 24)
     :vertical-align "middle"}]]

  [:#logo
   {:display "inline-block"
    :font {:size (px 24)
           :weight 700}
    :line-height (px 32)
    :color :#95C6ED}
   [:i
    {:color :#fff
     :margin [[0 (em 0.2)]]}]

   [:&:hover
    {:color :#fff}
    [:i {:color :#95C6ED}]]]

  )
