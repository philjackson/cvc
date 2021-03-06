(ns app.view.menu
  (:require [app.view.semantic :as s]
            [reagent.core :as r :refer [atom]]
            [app.firebase.auth :as auth]
            [reitit.frontend.easy :as rfe]
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
    [:div.signup-buttons
     [:button.social.google {:on-click (fn []
                                         (auth/sign-in-google)
                                         (close-login))}
      [s/icon {:name "google"}]
      " | Google"]
     [:button.social.github {:on-click (fn []
                                         (auth/sign-in-github)
                                         (close-login))}
      [s/icon {:name "github"}]
      " | Github"]]
    [:div.privacy-message
     [s/message {:info true
                 :content (r/as-element [:a {:href (rfe/href :privacy)
                                             :target "_blank"}
                                         "View our simple privacy policy."])}]]
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
       [:div.lower]]
      [:div.right
       (if (and user (not (empty? user)))
         [:<>
          [select-variation]
          [s/dropdown {:icon nil :trigger (r/as-element [:img.user-img {:src (:photo-url user)}])}
           [s/dropdown-menu
            [s/dropdown-item {:text "Logout" :on-click #(auth/sign-out!)}]]]]
         
         [:div.login-menu
          [:a {:href "#" :on-click open-login}
           "login / signup"]])]]]))
