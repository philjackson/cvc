(ns app.core
  (:require [reagent.core :as r :refer [atom]]
            [app.model.state :as state]
            [app.router :as router]
            [app.firebase.auth :as auth]))

(defn current-page []
  (let [view #p (state/view)]
    ))

(defn ^:dev/after-load render []
  (r/render [current-page]
            (.getElementById js/document "app")))

(defn ^:export main []
  (router/init)
  (render)
  (auth/init #(reset! state/user (if %
                                   (auth/extract-user %)
                                   nil))))
