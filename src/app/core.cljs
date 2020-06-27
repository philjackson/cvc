(ns app.core
  (:require [reagent.dom :as dom]
            [app.router :as router]
            [app.model.state :as state]
            [app.model.cv :as cv]
            [app.view.index :as index]
            [app.firebase.files :as files]
            [app.firebase.auth :as auth]))

(defn current-page []
  (let [user @state/user
        cvs @state/cvs]
    (cond
      (nil? user)
      [index/loader "Loading user..."]

      ;; we have all of our data, load the view, giving it a (css)
      ;; class name and passing in any parameters for convenience
      :else
      (let [{:keys [parameters]} (:data @state/match)]
        [@state/view parameters]))))

(defn ^:dev/after-load render []
  (router/init)
  (dom/render [current-page] (.getElementById js/document "app")))

(defn ^:export main []
  (render))
