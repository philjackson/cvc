(ns app.router
  (:require [reitit.frontend :as rf]
            [app.view.index :as view]
            [app.model.state :as state]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]))

(def routes
  [["/" {:name :index
         :view view/index
         :middleware []}]])

(defn init []
  (rfe/start!
   (rf/router routes {:conflicts nil
                      :data {:coercion rss/coercion}})
   (fn [match]
     ;; update the match wholesale
     (reset! state/match match)

     ;; run through the middleware
     (doseq [mw-fn (:middleware (:data match))] (mw-fn)))
   {:use-fragment false}))
