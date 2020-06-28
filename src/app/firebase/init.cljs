(ns app.firebase.init
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            ["firebase/analytics"]
            [app.firebase.config :refer [firebase-config]]))

(defn init []
  (.initializeApp firebase firebase-config)
  (.analytics firebase))
