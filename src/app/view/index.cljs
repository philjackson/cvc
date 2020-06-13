(ns app.view.index
  (:require [app.view.semantic :as s]
            [app.view.menu :as menu]
            [app.model.state :as state]
            [app.model.cv :refer [active-cv-path]]
            [app.view.cv :refer [cv-view]]
            [app.view.config :refer [config-view]]
            ["semantic-ui-react" :as sem]
            [reagent.core :refer [with-let atom cursor]]))

(defn loader [msg]
  [s/dimmer {:inverted true :active true}
   [s/loader {:size "massive"} msg]])

(defn builder-personal []
  [:> s/form
   [:h2 "Personal:"]
   [:<>
    [s/input-field {:atom (cursor state/cvs (active-cv-path @state/cvs :full-name))
                    :label "Full name:"}]
    [s/input-field {:atom (cursor state/cvs (active-cv-path @state/cvs :location))
                    :placeholder "London, UK"
                    :label "Location:"}]
    [s/input-field {:atom (cursor state/cvs (active-cv-path @state/cvs :job-title))
                    :label "Job title:"
                    :placeholder "CFO, advisor"}]
    [s/textarea {:atom (cursor state/cvs (active-cv-path @state/cvs :summary))
                 :info "Basic Markdown is supported in this field."
                 :label "Summary text:"}]]])

(defn index [params]
  [:<> 
   [menu/menu params]
   [:div.config-and-view.card
    [config-view params]
    [cv-view params]]])
