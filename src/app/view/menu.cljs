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
     (if (and user (not (empty? user)))
       [:div.right
        [select-variation]
        [s/dropdown {:icon nil :trigger (r/as-element [:img.user-img {:src (:photo-url user)}])}
         [s/dropdown-menu
          [s/dropdown-item {:text "Logout" :on-click #(auth/sign-out!)}]]]]
       [:div.login-menu
        [s/dropdown {:text "Login / signup"}
         [s/dropdown-menu
          [s/dropdown-item {:icon "google"
                            :text "With Google"
                            :on-click auth/sign-in-google}]
          [s/dropdown-item {:icon "github"
                            :text "With Github"
                            :on-click auth/sign-in-github}]]]])]))
