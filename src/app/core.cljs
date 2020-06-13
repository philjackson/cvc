(ns app.core
  (:require [reagent.dom :as dom]
            [app.router :as router]
            [app.view.index :as index]
            [app.model.state :as state]
            [app.firebase.auth :as auth]))

(defn current-page []
  (let [user @state/user
        cvs @state/cvs]
    (cond
      (not user)
      [index/loader "Loading user..."]

      (not cvs)
      [index/loader "Loading your CVs..."]

      ;; we have all of our data, load the view, giving it a (css)
      ;; class name and passing in any parameters for convenience
      :else
      (let [{:keys [parameters]} (:data @state/match)]
        [@state/view parameters]))))

(defn ^:dev/after-load render []
  (dom/render
   [current-page]
   (.getElementById js/document "app")))

(defn ^:export main []
  (router/init)
  (render)
  (auth/init #(reset! state/user (if %
                                   (auth/extract-user %)
                                   {}))))
