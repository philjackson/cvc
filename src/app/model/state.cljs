(ns app.model.state
  (:require [reagent.core :as r :refer [atom]]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :refer [>defn => | ?]]
            [app.debug :refer [debug?]]))

(defonce all-seeing-state (atom {:cv-data {}
                                 :route-match nil
                                 :config {}}))

(s/def ::id string?)
(s/def ::cv-data (s/keys :req-op [::id]))
(s/def ::config (s/keys))
(s/def ::route-match (s/nilable (s/keys)))

(s/def ::uid string?)
(s/def ::display-name string?)
(s/def ::email string?)

(s/def ::user (s/or :valid-user (s/keys :req-un [::uid ::display-name ::email])
                    :no-user #(= % {})))
(s/def ::state (s/keys :req-un [::config ::cv-data ::route-match]
                       :opt-un [::user]))

;; cursors for the most commonly accessed parts
(def config (r/cursor all-seeing-state [:config]))
(def user   (r/cursor all-seeing-state [:user]))
(def cvs    (r/cursor all-seeing-state [:cv-data]))
(def match  (r/cursor all-seeing-state [:route-match]))
(def view   (r/cursor all-seeing-state [:route-match :data :view]))

#_(when debug?
    (s/explain ::cv-data @cvs)
    (s/explain ::config @config)
    (s/explain ::user @user)
    (s/explain ::route-match @match))
