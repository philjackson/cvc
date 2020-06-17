(ns app.view.menu
  (:require [app.view.semantic :as s]
            [app.view.variation-switcher :refer [select-variation]]
            [app.model.state :as state]))

(defn menu [params]
  (let [user @state/user]
    [:div.navbar.card
     [:div.left 
      [:div.upper
       [:div.title "Next CV"]]
      [:div.lower
       [:a {:href "/"} "home"]]]
     (when user
       [:div.right
        [select-variation]
        [:img.user-img {:src (:photo-url user)}]])]))
