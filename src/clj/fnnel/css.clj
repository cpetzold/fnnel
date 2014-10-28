(ns fnnel.css
  (:require
   [garden.def :refer [defstyles]]
   [garden.units :refer [px em percent]]))

(defstyles screen
  [:* {:box-sizing "border-box"}]

  [:body
   {:font {:size (px 18)
           :family 'Helvetica}
    :line-height 1.4
    :background {:color :#202026}
    :color :#fff
    :margin 0}]

  [:a
   {:text-decoration "none"}]

  [:h1 :h2 :h3 :h4
   {:margin 0}]

  [:p
   {:margin [[(em 0.5) 0]]}])
