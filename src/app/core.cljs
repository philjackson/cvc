(ns app.core
  (:require [reagent.dom :as dom]
            [app.router :as router]
            [app.model.state :as state]
            [app.model.cv :as cv]
            [app.view.index :as index]
            [app.model.state :as state]
            [app.firebase.auth :as auth]))

(defn current-page []
  (let [user @state/user
        cvs @state/cvs]
    (cond
      (not user)
      [index/loader "Loading user..."]

      (not (:selected cvs))
      [index/loader "Loading your CVs..."]

      ;; we have all of our data, load the view, giving it a (css)
      ;; class name and passing in any parameters for convenience
      :else
      (let [{:keys [parameters]} (:data @state/match)]
        [@state/view parameters] ))))

(defn ^:dev/after-load render []
  (router/init)
  (dom/render [current-page] (.getElementById js/document "app")))

(defn ^:export main []
  (render)
  (auth/init (fn [user]
               (if user 
                 ;; we've a valid user so fetch their CV data
                 (do
                   (reset! state/user (auth/extract-user user))
                   (let [new-id (random-uuid)
                         temp-cv (-> @state/cvs
                                     (cv/add {:id new-id})
                                     (cv/select new-id))]
                     (reset! state/cvs temp-cv)))
                 (reset! state/user (auth/extract-user user))))))
