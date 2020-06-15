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
      [:a {:href (rfe/href :personal {:cv-id (cv/selected @state/cvs)})} "personal"]
      [:a {:href (rfe/href :education {:cv-id (cv/selected @state/cvs)})} "education"]
      [:a {:href (rfe/href :workexp {:cv-id (cv/selected @state/cvs)})} "workexp"]
      [:a {:href (rfe/href :references {:cv-id (cv/selected @state/cvs)})} "references"]

      [builder-view]
      [cv-view params]]
     (when debug?
       [:pre (with-out-str (cljs.pprint/pprint @state/cvs))])]))
