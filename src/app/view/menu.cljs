(ns app.view.menu
  (:require [app.view.semantic :as s]
            [reagent.core :as r :refer [atom]]
            [app.firebase.auth :as auth]
            [app.view.variation-switcher :refer [select-variation]]
            [app.model.state :as state]))

(defn login-modal [open?]
  (let [close #(reset! open? false)]
    (fn [open?]
      [s/modal {:on-close close
                :open @open?
                :size "small"
                :close-icon true}
       [s/modal-header "Login / Signup"]
       [s/modal-content
        [:div [:button.social.google {:on-click (fn []
                                                  (auth/sign-in-google)
                                                  (close))}
               [s/icon {:name "google"}]
               " | Google"]]
        [:div [:button.social.github {:on-click (fn []
                                                  (auth/sign-in-google)
                                                  (close))}
               [s/icon {:name "github"}]
               " | Gihub"]]
        [s/divider]
        [s/modal-actions
         [s/button {:primary true :on-click close} "Cancel"]]]])))

(defn menu [params]
  (r/with-let [login-open? (atom false)]
    (let [user @state/user]
      [:<>
       [login-modal login-open?]

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
           [:a {:href "#" :on-click #(reset! login-open? true)}
            "login / signup"]])]])))
