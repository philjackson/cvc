(ns app.model.state
  (:require [reagent.core :as r :refer [atom]]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :as g :refer [>defn => | ?]]
            [app.debug :refer [debug?]]))

(def all-seeing-state (atom {:cv-data {}
                             :config {}}))

(s/def ::cv-data (s/keys))
(s/def ::config (s/keys))

(s/def ::uid string?)
(s/def ::display-name string?)
(s/def ::email string?)
(s/def ::user (s/nilable (s/keys :req-un [::uid ::display-name ::email])))

;; user being optional as it's how we can tell if the user has been
;; initialised
(s/def ::state (s/keys :req-un [::config ::cv-data]
                       :opt-un [::user]))

;; cursors for the most commonly accessed parts
(def config (r/cursor all-seeing-state [:config]))
(def user   (r/cursor all-seeing-state [:user]))
(def cvs    (r/cursor all-seeing-state [:cv-data]))

(when debug?
  (s/explain ::cv-data @cvs)
  (s/explain ::config @config)
  (s/explain ::user @user))
