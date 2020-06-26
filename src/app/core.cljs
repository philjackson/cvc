(ns app.core
  (:require [reagent.dom :as dom]
            [app.router :as router]
            [app.model.state :as state]
            [app.model.cv :as cv]
            [app.view.index :as index]
            [app.firebase.files :as files]
            [app.truthy :refer [truthy?]]
            [app.firebase.auth :as auth]))

(defn update-title! []
  (let [title (cond-> ""
                (truthy? (get-in @state/cvs (cv/active-cv-path @state/cvs :full-name)))
                (str (get-in @state/cvs (cv/active-cv-path @state/cvs :full-name)))

                (truthy? (get-in @state/cvs (cv/active-cv-path @state/cvs :job-title)))
                (str " - " (get-in @state/cvs (cv/active-cv-path @state/cvs :job-title))))]
    (set! (.-title js/document) title)))

(defn on-cv-update
  "When the user is loggedin via FB, we use their file store, otherwise,
  we use the localstorage."
  [_ new-details]
  (when-not (:updating-storage? @state/config)
    ;; let the rest of the app know we're updating firebase
    (swap! state/config assoc :updating-storage? true)
    ;; in five seconds, update storage. This will block other attempts
    ;; to do so through the :updating-fb lock.
    (js/setTimeout (fn []
                     (update-title!)
                     (when-let [user @state/user]
                       (files/upload-file @state/cvs (:uid user)))
                     (swap! state/config dissoc :updating-storage?))
                   3000)))

(defn current-page []
  (let [user @state/user
        cvs @state/cvs]
    (cond
      (nil? user)
      [index/loader "Loading user..."]

      ;; we've tried to load a user, but there isn't one
      (and (map? user) (empty? user))
      [index/front-page]

      (not (:selected cvs))
      [index/loader "Loading your CVs..."]

      ;; we have all of our data, load the view, giving it a (css)
      ;; class name and passing in any parameters for convenience
      :else
      (let [{:keys [parameters]} (:data @state/match)]
        [@state/view parameters]))))

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
                   ;; try to download the user's CVs
                   (files/download-file
                    (:uid @state/user)
                    (fn [data]
                      (if data
                        (reset! state/cvs data)
                        ;; we create a new CV as there's nothing to
                        ;; download
                        (let [new-id (random-uuid)]
                          (print "No CV found in the cloud, building a new one.")
                          (reset! state/cvs (-> @state/cvs
                                                (cv/add {:id new-id :name "Main"})
                                                (cv/select new-id)))))
                      (add-watch state/cvs :cv-cursor-watcher on-cv-update))))
                 ;; we've tried to auth but the user isn't signed in
                 (reset! state/user {})))))
