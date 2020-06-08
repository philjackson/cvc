(ns app.view.index
  (:require [app.view.semantic :as s]))

(defn loader [msg]
  [:> s/dimmer {:inverted true :active true}
   [:> s/loader {:size "massive"} msg]])

(defn header [])

(defn index [params]
  [:<>
   [header]
   [:div "index"]])
