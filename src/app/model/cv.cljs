(ns app.model.cv
  #:ghostwheel.core{:check true :num-tests 10}
  (:require [app.model.state :as state]
            [cognitect.transit :as transit]
            [app.debug :refer [debug?]]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :as g
             :refer [>defn => ?]]))

(>defn select
  [cv-state id]
  [::state/cvs ::state/id => ::state/cvs]
  (assoc cv-state :selected id))

(>defn add
  [cv-state cv]
  [::state/cvs ::state/cv => ::state/cvs]
  (assoc-in cv-state [:docs (:id cv)] cv))

(>defn selected
  [cv-state]
  [::state/cvs => (? ::state/id)]
  (:selected cv-state))

(>defn active-cv-path
  ([cv-state]
   [::state/cvs => (? coll?)]
   (when-let [sel (selected cv-state)]
     [:docs (selected cv-state)]))
  ([cv-state & extra-paths]
   [::state/cvs (s/coll-of string?) => coll?]
   (concat (active-cv-path cv-state) extra-paths)))

(>defn does-exist?
  [cv-state id]
  [::state/cvs ::state/id => boolean?]
  (contains? (:docs cv-state) id))

(s/def ::js-blob #(instance? js/Blob %))
(>defn cv->blob
  [cv-state]
  [::state/cvs => ::js-blob]
  (js/Blob. [(transit/write (transit/writer :json) cv-state)]
            #js {:type "application/edn;charset=utf-8"}))
