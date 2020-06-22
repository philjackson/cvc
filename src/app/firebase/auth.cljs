(ns app.firebase.auth
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            ["firebase/analytics"]
            [app.model.state :as state]
            [app.firebase.config :refer [firebase-config]]))

(defn extract-user [firebase-user]
  (when firebase-user
    (let [provider-data (if-not (empty? (.-providerData firebase-user))
                          (-> (.-providerData firebase-user)
                              first
                              (js->clj :keywordize-keys true))
                          {})]
      {:uid           (.-uid firebase-user)
       :provider-data provider-data
       :display-name  (.-displayName firebase-user)
       :photo-url     (.-photoURL firebase-user)
       :email         (:email provider-data)})))

(defn init [on-loaded]
  (.initializeApp firebase firebase-config)
  (.analytics firebase)
  (.onAuthStateChanged (.auth firebase) on-loaded))

(defn sign-out! []
  (-> (.signOut (.auth firebase))
      (.then (fn [] (reset! state/user nil)))
      (.catch (fn [js-result] (.log js/console js-result)))))

(defn sign-in [provider app]
  (-> (.setPersistence (.auth firebase) (.. firebase -auth -Auth -Persistence -LOCAL))
      (.then (fn []
               (let [provider (new (aget (.-auth firebase) provider))]
                 (-> (.signInWithPopup (.auth firebase) provider)
                     (.catch (fn [js-result] (.log js/console js-result)))))))))

(def sign-in-github (partial sign-in "GithubAuthProvider"))
(def sign-in-google (partial sign-in "GoogleAuthProvider"))
