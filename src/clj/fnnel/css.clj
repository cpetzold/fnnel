(ns fnnel.css
  (:refer-clojure :exclude [rem])
  (:require
   [garden.def :refer [defstyles defcssfn]]
   [garden.units :refer [px em rem percent vh vw]]
   [garden.color :as color]))

(defn set-alpha [color a]
  (-> color
      name
      color/hex->rgb
      (assoc :alpha a)))

(def sans-stack
  ["Helvetica Neue" "Helvetica" "sans-serif"])

(def page-width 700)
(def page-padding 32)

(defcssfn -webkit-linear-gradient)

(defstyles screen
  [:* {:box-sizing "border-box"}]

  [:body
   {:min-width (px (+ page-width (* 2 page-padding)))
    :font {:size (px 14)
           :family sans-stack}
    :line-height 1.4
    :background {:color :#fff}
    :color :#111
    :margin 0}]

  [:.container
   {:position "relative"
    :width (px page-width)
    :margin [[0 "auto"]]}]

  [:a
   {:text-decoration "none"
    :color "inherit"}]

  [:h1 :h2 :h3 :h4 :ul :ol :p
   {:margin 0
    :padding 0}]

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
    :border {:radius (px 3)}
    :background {:size "cover"}
    :vertical-align "middle"}

   (for [[sel size] {:&.small 20
                     :&.medium 24
                     :&.large 32}
         :let [size (px size)]]
     [sel {:width size :height size}])]

  [:.text-small
   {:font {:size (rem 0.8)}}]

  [:.text-large
   {:font {:size (rem 1.2)}}]

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

  [:#header
   {:width (percent 100)
    :padding [[(px 8) 0]]
    :overflow "hidden"
    :background :#202026
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
   {:display "inline-block"}]

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

  [:.pagehead
   {:padding [[(rem 1) 0]]
    :background (set-alpha "#fff" 0.98)
    :border {:bottom [[(px 1) :solid :#eaeaea]]}}]

  [:.sticky
   {:position "-webkit-sticky"
    :top (px 0)
    :z-index 1}]

  [:.content
   {:padding [[(rem 1) 0]]
    :min-height (vh 200)}

   ["> div"
    {:margin {:bottom (rem 0.5)}}]]

  [:.function-arglists
   {:list-style "none"
    :font {:size (rem 1.2)}
    :color :#666}

   [:b {:color :#111}]]

  [:.function-arglist
   {:display "inline-block"
    :margin {:right (rem 1)}}]

  [:.box
   {:padding (px 8)
    :border {:radius (px 3)}
    :background :#eee
    :color :#777}]

  [:.function-author
   {:color :#444}
   [:.avatar
    {:margin {:right (px 4)}}]])
