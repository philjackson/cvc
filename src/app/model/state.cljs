(ns app.model.state
  (:require [reagent.core :as r :refer [atom]]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :refer [>defn => | ?]]
            [app.debug :refer [debug?]]))

(defonce all-seeing-state (atom {:cv-data {}
                                 :route-match nil
                                 :config {}}))

(s/def ::cv-data (s/keys))
(s/def ::config (s/keys))
(s/def ::route-match (s/nilable (s/keys)))

(s/def ::uid string?)
(s/def ::display-name string?)
(s/def ::email string?)

;; user being optional as it's how we can tell if the user has been
;; initialised
(s/def ::user (s/nilable (s/keys :req-un [::uid ::display-name ::email])))
(s/def ::state (s/keys :req-un [::config ::cv-data ::route-match]
                       :opt-un [::user]))

;; cursors for the most commonly accessed parts
(def config (r/cursor all-seeing-state [:config]))
(def user   (r/cursor all-seeing-state [:user]))
(def cvs    (r/cursor all-seeing-state [:cv-data]))
(def match  (r/cursor all-seeing-state [:route-match]))
(def view   (r/cursor all-seeing-state [:route-match :data :view]))

(when debug?
  (s/explain ::cv-data @cvs)
  (s/explain ::config @config)
  (s/explain ::user @user)
  (s/explain ::route-match @match))
