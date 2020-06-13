(ns app.model.state
  (:require [reagent.core :as r :refer [atom]]
            [cljs.spec.alpha :as s]
            [app.debug :refer [debug?]]
            [ghostwheel.core :refer [>defn => | ?]]))

(def non-blank-str? #(not (clojure.string/blank? %)))

(defonce all-seeing-state (atom {:cvs {:selected nil
                                       :docs []}
                                 :route-match nil
                                 :user nil
                                 :config {}}))

(s/def ::id uuid?)
(s/def ::selected (s/nilable ::id))
(s/def ::cvs (s/keys :req-un [::selected ::docs]))
(s/def ::docs (s/coll-of ::cv))
(s/def ::cv (s/keys :req-un [::id]))

(s/def ::config (s/keys))
(s/def ::route-match (s/nilable (s/keys)))

(s/def ::uid (s/and non-blank-str? string?))
(s/def ::display-name string?)

(s/def ::email (s/and non-blank-str?
                      string?
                      #(re-matches #".+?@.+?\..+" %)))

(s/def ::user (s/or :not-initialised     nil?
                    :valid-firebase-user (s/keys :req-un [::uid ::display-name ::email])
                    :no-firebase-user    #{{}}))

(s/def ::state (s/keys :req-un [::user ::config ::cvs ::route-match]))

;; cursors for the most commonly accessed parts
(def config (r/cursor all-seeing-state [:config]))
(def user   (r/cursor all-seeing-state [:user]))
(def cvs    (r/cursor all-seeing-state [:cv-data]))
(def match  (r/cursor all-seeing-state [:route-match]))
(def view   (r/cursor all-seeing-state [:route-match :data :view]))
