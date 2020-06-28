(ns app.router
  (:require [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [app.view.index :as view]
            [app.model.state :as state]
            [app.view.builders :as builders]
            [app.model.cv :as cv]
            [app.firebase.files :as files]
            [app.firebase.auth :as auth]
            [app.truthy :refer [truthy?]]
            [reitit.coercion.spec :as rss]))

(defn update-title! []
  (let [title (cond-> ""
                (truthy? (-> @state/cvs
                             cv/selected
                             :full-name))
                (str (-> @state/cvs
                         cv/selected
                         :full-name))

                (truthy? (-> @state/cvs
                             cv/selected
                             :job-title))
                (str " - " (-> @state/cvs
                               cv/selected
                               :job-title)))]
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
                     (files/upload-all-files!)
                     (swap! state/config dissoc :updating-storage?))
                   3000)))

(defn set-anonymous-user [match done]
  (reset! state/user {}))

(defn authenticate-user [match done]
  ;; only load the user once
  (if (nil? @state/user)
    (auth/init (fn [user]
                 (reset! state/user
                         (if user
                           (auth/extract-user user)
                           {}))
                 (done)))
    (done)))

(defn download-private-cv [match done]
  ;; if we don't have CVs
  (if (empty? (:docs @state/cvs))
    (files/download-file
     (files/get-private-filename (:uid @state/user))
     (fn [data]
       (if data
         (reset! state/cvs data)
         ;; we create a new CV as there's nothing to download
         (let [new-id (random-uuid)]
           (print "No CV found in the cloud, building a new one.")
           (reset! state/cvs (-> @state/cvs
                                 (cv/add {:id new-id :name "Main"})
                                 (cv/select new-id)))))
       (add-watch state/cvs :cv-cursor-watcher on-cv-update)
       (done)))
    (done)))

(defn select-cv [match done]
  (let [cv-id (uuid (-> match
                        :parameters
                        :path
                        :cv-id))]
    (reset! state/cvs (cv/select @state/cvs cv-id))
    (done)))

(defn download-public-cv [match done]
  (files/download-file
   (files/get-public-filename (-> match
                                  :parameters
                                  :path
                                  :cv-id))
   (fn [data]
     (when data
       (reset! state/cvs (-> @state/cvs
                             (cv/add data)
                             (cv/select(:id data)))))
     (done)))
  (done))

(def routes
  [["/" {:name :index
         :selected :personal
         :middleware [authenticate-user download-private-cv]
         :view (view/index-dispatcher builders/personal)}]
   ["/pub/:cv-id" {:name :public
                   :middleware [download-public-cv
                                set-anonymous-user]
                   :view view/public-cv}]
   ["/cv/:cv-id/personal" {:name :personal
                           :selected :personal
                           :middleware [authenticate-user
                                        download-private-cv
                                        select-cv]
                           :view (view/index-dispatcher builders/personal)}]
   ["/cv/:cv-id/contact" {:name :contact
                          :selected :contact
                          :middleware [authenticate-user
                                       download-private-cv
                                       select-cv]
                          :view (view/index-dispatcher builders/contact)}]
   ["/cv/:cv-id/workexp" {:name :workexp
                          :selected :workexp
                          :middleware [authenticate-user
                                       download-private-cv
                                       select-cv]
                          :view (view/index-dispatcher builders/workexp)}]
   ["/cv/:cv-id/education" {:name :education
                            :selected :education
                            :middleware [authenticate-user
                                         download-private-cv
                                         select-cv]
                            :view (view/index-dispatcher builders/education)}]
   ["/cv/:cv-id/references" {:name :references
                             :selected :references
                             :middleware [authenticate-user
                                          download-private-cv
                                          select-cv]
                             :view (view/index-dispatcher builders/references)}]])

(defn four-o-four []
  [:div "404"])

(defn run-middleware
  "Threads the req & res atoms through each middleware fn.
  Returns response atom value."
  [middleware match]
  (let [[middleware-fn & rest] middleware]
    (when middleware-fn 
      (middleware-fn match #(run-middleware rest match)))))

(defn init []
  (rfe/start!
   (rf/router routes {:conflicts nil
                      :data {:coercion rss/coercion}})
   (fn [match]
     ;; update the match wholesale
     (reset! state/match match)

     ;; run through the middleware
     (run-middleware (:middleware (:data match)) match))
   {:use-fragment false}))
