(ns app.core
  (:require [reagent.dom :as dom]
            [app.router :as router]
            [app.model.state :as state]
            [app.firebase.init :as firebase]
            [app.view.index :as index]))

(defn current-page []
  (let [user @state/user]
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
  (firebase/init)
  (render))
