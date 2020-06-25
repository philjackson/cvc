(ns app.view.menu
  (:require [app.view.semantic :as s]
            [reagent.core :as r :refer [atom]]
            [app.firebase.auth :as auth]
            [app.view.variation-switcher :refer [select-variation]]
            [app.model.state :as state]))

(defonce modal-open? (atom false))

(defn open-login []
  (reset! modal-open? true))

(defn close-login []
  (reset! modal-open? false))

(defn login-modal []
  [s/modal {:on-close close-login
            :open @modal-open?
            :size "small"
            :close-icon true}
   [s/modal-header "Login / Signup"]
   [s/modal-content
    [:div [:button.social.google {:on-click (fn []
                                              (auth/sign-in-google)
                                              (close-login))}
           [s/icon {:name "google"}]
           " | Google"]]
    [:div [:button.social.github {:on-click (fn []
                                              (auth/sign-in-google)
                                              (close-login))}
           [s/icon {:name "github"}]
           " | Gihub"]]
    [s/divider]
    [s/modal-actions
     [s/button {:primary true :on-click close-login} "Cancel"]]]])

(defn menu [params]
  (let [user @state/user]
    [:<>
     [login-modal]

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
         [:a {:href "#" :on-click open-login}
          "login / signup"]])]]))
