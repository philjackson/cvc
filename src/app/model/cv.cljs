(ns app.model.cv
  #:ghostwheel.core{:check true :num-tests 20}
  (:require [app.model.state :as state]
            [cognitect.transit :as transit]
            [app.debug :refer [debug?]]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :as g
             :refer [>defn => ? |]]))

(>defn select
  "Select the given CV within `cv-state`."
  [cv-state id]
  [::state/cvs ::state/id => ::state/cvs]
  (assoc cv-state :selected id))

(>defn add
  "Add a new CV to `cv-state`."
  [cv-state cv]
  [::state/cvs ::state/cv => ::state/cvs]
  (assoc-in cv-state [:docs (:id cv)] cv))

(>defn selected
  "Returns the ID of the currently selected CV."
  [cv-state]
  [::state/cvs => (? ::state/id)]
  (:selected cv-state))

(>defn delete
  "Remove a CV and un-select it if need be."
  [cv-state id]
  [::state/cvs ::state/id => ::state/cvs | #(not= id (:id (selected cv-state)))]
  (let [next (update-in cv-state [:docs] dissoc id)]
    (if (= (:id (selected next)) id)
      (assoc next :selected nil)
      next)))

(>defn cv-get
  [cv-state id]
  [::state/cvs ::state/id => (? ::state/cv)]
  (get-in cv-state [:docs id]))

(>defn active-cv-path
  "Gives the path (suitable for `get-in` and co.) to the currently
  selected CV."
  ([cv-state]
   [::state/cvs => (? coll?)]
   (when-let [sel (selected cv-state)]
     [:docs (selected cv-state)]))
  ([cv-state & extra-paths]
   [::state/cvs (s/coll-of string?) => coll?]
   (concat (active-cv-path cv-state) extra-paths)))

(>defn does-exist?
  "Does the passed in CV id exist in docs?"
  [cv-state id]
  [::state/cvs ::state/id => boolean?]
  (contains? (:docs cv-state) id))

(s/def ::js-blob #(instance? js/Blob %))
(>defn cv->blob
  "Convert the passed in cv-state to a Javascript Blob, ready to
  upload."
  [cv-state]
  [::state/cvs => ::js-blob]
  (js/Blob. [(transit/write (transit/writer :json) cv-state)]
            #js {:type "application/edn;charset=utf-8"}))

(>defn is-valid?
  "Very basic validation function for downloaded cvs. Selected must be
  filled."
  [cvs]
  [any? => boolean?]
  (and (contains? cvs :selected)
       (uuid? (:selected cvs))

       (contains? cvs :docs)
       (map? (:docs cvs))))

(>defn cv-merge
  [cv-one cv-two]
  [::state/cv ::state/cv => ::state/cv]
  (merge cv-one cv-two))

(>defn public-cvs
  [cv-state]
  [::state/cvs => (s/coll-of ::state/cv)]
  (filter :public? (vals (:docs cv-state))))
