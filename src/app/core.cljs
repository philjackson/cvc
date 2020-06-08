(ns app.core
  (:require [reagent.core :as r :refer [atom]]
            [app.model.state :as state]
            [app.firebase.auth :as auth]))

(defn ^:dev/after-load render []
  )

(defn ^:export main []
  (render)
  (auth/init #(reset! state/user (if %
                                   (auth/extract-user %)
                                   nil))))
