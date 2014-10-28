(ns fnnel.dispatch)

(defmulti dispatch! (fn [state type data] type))
