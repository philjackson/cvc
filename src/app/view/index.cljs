(ns app.view.index
  (:require [app.view.semantic :as s]
            [app.debug :refer [debug?]]
            [app.view.menu :as menu]
            [app.model.cv :as cv]
            [reitit.frontend.easy :as rfe]
            [app.model.state :as state]
            [app.view.cv :refer [cv-view]]))

(defn loader [msg]
  [s/dimmer {:inverted true :active true}
   [s/loader {:size "massive"} msg]])

(defn index [builder-view]
  (fn [params]
    [:<>
     [menu/menu params]
     [:div.builder-and-view.card
      [:div.builder
       [:div.builder-links
        [:a {:href (rfe/href :personal {:cv-id (cv/selected @state/cvs)})} "personal"]
        [:a {:href (rfe/href :education {:cv-id (cv/selected @state/cvs)})} "education"]
        [:a {:href (rfe/href :workexp {:cv-id (cv/selected @state/cvs)})} "workexp"]
        [:a {:href (rfe/href :references {:cv-id (cv/selected @state/cvs)})} "references"]]
       [builder-view]]
      [cv-view params]]
     (when debug?
       [:<>
        [:pre (with-out-str (cljs.pprint/pprint @state/cvs))]
        [:button {:on-click (fn []
                              (rfe/push-state :index)
                              (reset! state/cvs (let [new-id (random-uuid)]
                                                  (-> (state/initial-state)
                                                      :cvs
                                                      (cv/add {:id new-id :name "Main"})
                                                      (cv/select new-id)))))}
         "Delete all data"]])]))
