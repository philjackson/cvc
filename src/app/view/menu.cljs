(ns app.view.menu
  (:require [app.view.semantic :as s]
            [reagent.core :as r]
            [app.firebase.auth :as auth]
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
        [s/dropdown {:trigger (r/as-element [:img.user-img {:src (:photo-url user)}])}
         [s/dropdown-menu
          [s/dropdown-item {:text "Logout"
                            :on-click #(auth/sign-out!)}]]]])]))
