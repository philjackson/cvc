(ns app.view.index
  (:require [app.view.semantic :as s]
            [app.debug :refer [debug?]]
            [app.view.menu :as menu]
            [app.model.cv :as cv]
            [reitit.frontend.easy :as rfe]
            [reagent.core :as r]
            [app.model.state :as state]
            [app.view.cv :refer [cv-view]]))

(defn loader [msg]
  [s/dimmer {:inverted true :active true}
   [s/loader {:size "massive"} msg]])

(defn front-page []
  [:div "hi"])

(defn index [builder-view]
  (fn [params]
    [:<>
     [menu/menu params]
     [:div.builder-and-cv.card
      [:div.builder
       [:div.builder-links
        (for [item [:personal :contact :education :workexp :references]]
          [:a {:href (rfe/href item {:cv-id (cv/selected @state/cvs)})} (str item)])]
       [builder-view]]
      [:div#cv-column
       [:div#cv
        [cv-view params (r/cursor state/cvs (cv/active-cv-path @state/cvs))]]]]
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
