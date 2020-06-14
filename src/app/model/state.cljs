(ns app.model.state
  #:ghostwheel.core{:check true :num-tests 10}
  (:require [reagent.core :as r :refer [atom]]
            [cljs.spec.alpha :as s]
            [app.debug :refer [debug?]]
            [ghostwheel.core :as g :refer [>defn => ?]]))

(def non-blank-str? #(not (clojure.string/blank? %)))

(s/def ::id uuid?)
(s/def ::selected (? ::id))
(s/def ::cvs (s/keys :req-un [::selected ::docs]))
(s/def ::docs (s/map-of ::id ::cv))
(s/def ::cv (s/keys :req-un [::id]))

(s/def ::config (s/keys))

(s/def ::route-match (? (s/keys)))

(s/def ::uid (s/and string? non-blank-str?))
(s/def ::display-name string?)
(s/def ::email string?)
(s/def ::user (s/or :not-initialised     nil?
                    :valid-firebase-user (s/keys :req-un [::uid ::display-name ::email])
                    :no-firebase-user    #{{}}))

(s/def ::state (s/keys :req-un [::user ::config ::cvs ::route-match]))

(>defn initial-state
  []
  [=> ::state]
  {:cvs {:selected nil
         :docs {}}
   :route-match nil
   :user nil
   :config {}})

(defonce all-seeing-state (atom (initial-state)))

;; cursors for the most commonly accessed parts
(def config (r/cursor all-seeing-state [:config]))
(def user   (r/cursor all-seeing-state [:user]))
(def cvs    (r/cursor all-seeing-state [:cvs]))
(def match  (r/cursor all-seeing-state [:route-match]))
(def view   (r/cursor all-seeing-state [:route-match :data :view]))
