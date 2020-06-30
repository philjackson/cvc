(ns app.firebase.auth
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            [app.model.state :as state]))

(defn init [on-loaded]
  (.onAuthStateChanged (.auth firebase) on-loaded))

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

(defn sign-out! []
  (-> (.signOut (.auth firebase))
      (.then (fn [] (reset! state/user nil)))
      (.catch (fn [js-result] (.log js/console js-result)))))

(defn sign-in [provider app]
  (-> (.setPersistence (.auth firebase) (.. firebase -auth -Auth -Persistence -LOCAL))
      (.then (fn []
               (let [provider (new (aget (.-auth firebase) provider))]
                 (-> (.signInWithRedirect (.auth firebase) provider)
                     (.catch (fn [js-result] (.log js/console js-result)))))))))

(def sign-in-github (partial sign-in "GithubAuthProvider"))
(def sign-in-google (partial sign-in "GoogleAuthProvider"))
