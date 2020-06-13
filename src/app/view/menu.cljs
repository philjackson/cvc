(ns app.view.menu
  (:require [reagent.core :as reagent :refer [atom with-let]]
            [reitit.frontend.easy :as rfe]
            ["semantic-ui-react" :as sem]
            [app.view.semantic :as s]
            [app.model.state :as state]))

(defn menu [params]
  (let [user @state/user]
    [:div.navbar.card
     [:div.left 
      [:div.upper
       [:div.title "Next CV"]]
      [:div.lower
       [:a {:href "/"} "home"]]]
     (when #p user
       [:div.right
        [:img.user-img {:src (:photo-url user)}]])]))
