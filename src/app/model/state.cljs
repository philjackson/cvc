(ns app.model.state
  (:require [reagent.core :as r :refer [atom]]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :refer [>defn => | ?]]
            [app.debug :refer [debug?]]))

(defonce all-seeing-state (atom {:cv-data {}
                                 :route-match nil
                                 :user nil
                                 :config {}}))

(s/def ::id string?)
(s/def ::cv-data (s/keys :req-op [::id]))
(s/def ::config (s/keys))
(s/def ::route-match (s/nilable (s/keys)))

(s/def ::uid string?)
(s/def ::display-name string?)
(s/def ::email (s/and string? #(re-matches #".+?@.+?\..+" %)))

(s/def ::user (s/or :not-initialised     nil?
                    :valid-firebase-user (s/keys :req-un [::uid ::display-name ::email])
                    :no-firebase-user    #(= % {})))

(s/def ::state (s/keys :req-un [::user ::config ::cv-data ::route-match]))

;; cursors for the most commonly accessed parts
(def config (r/cursor all-seeing-state [:config]))
(def user   (r/cursor all-seeing-state [:user]))
(def cvs    (r/cursor all-seeing-state [:cv-data]))
(def match  (r/cursor all-seeing-state [:route-match]))
(def view   (r/cursor all-seeing-state [:route-match :data :view]))
