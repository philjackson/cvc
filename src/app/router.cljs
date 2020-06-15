(ns app.router
  (:require [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [app.view.index :as view]
            [app.model.state :as state]
            [app.view.builders :as builders]
            [app.model.cv :as cv]
            [reitit.coercion.spec :as rss]))

(defn select-cv [match]
  (let [cv-id (uuid (-> match
                        :parameters
                        :path
                        :cv-id))]
    ;; TODO: sort out a way to 404
    #_(when-not (cv/does-exist? @state/cvs cv-id)
        (throw (js/Error. {:type :four-o-four
                           :message "The requested CV doesn't exist."})))
    (reset! state/cvs (cv/select @state/cvs cv-id))))

(def routes
  [["/" {:name :index
         :view (view/index builders/personal)}]
   ["/cv/:cv-id/personal" {:name :personal
                           :middleware [select-cv]
                           :view (view/index builders/personal)}]
   ["/cv/:cv-id/workexp" {:name :workexp
                          :middleware [select-cv]
                          :view (view/index builders/workexp)}]
   ["/cv/:cv-id/education" {:name :education
                            :middleware [select-cv]
                            :view (view/index builders/education)}]
   ["/cv/:cv-id/references" {:name :references
                             :middleware [select-cv]
                             :view (view/index builders/references)}]])

(defn four-o-four []
  [:div "404"])

(defn init []
  (rfe/start!
   (rf/router routes {:conflicts nil
                      :data {:coercion rss/coercion}})
   (fn [match]
     ;; update the match wholesale
     (reset! state/match match)

     ;; run through the middleware
     (doseq [mw-fn (:middleware (:data match))] (mw-fn match)))
   {:use-fragment false}))
