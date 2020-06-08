(ns app.core
  (:require [reagent.dom :as dom]
            [app.model.state :as state]
            [app.router :as router]
            [app.firebase.auth :as auth]))

(defn current-page []
  (let [view @state/view
        user @state/user
        cvs @state/cvs]
    (if (or (empty? user) (not cvs))
      [:div "Loading user and data."]
      view)))

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
