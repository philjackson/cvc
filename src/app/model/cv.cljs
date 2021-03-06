(ns app.model.cv
  #:ghostwheel.core{:check true :num-tests 10}
  (:require [app.model.state :as state]
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

(>defn selected-id
  "Returns the ID of the currently selected CV."
  [cv-state]
  [::state/cvs => (? ::state/id)]
  (:selected cv-state))

(>defn cv-get
  [cv-state id]
  [::state/cvs ::state/id => (? ::state/cv)]
  (get-in cv-state [:docs id]))

(>defn selected
  "Returns the actual CV that's selected."
  [cv-state]
  [::state/cvs => (? ::state/cv)]
  (when-let [selected (selected-id cv-state)]
    (cv-get cv-state selected)))

(>defn delete
  "Remove a CV and un-select it if need be."
  [cv-state id]
  [::state/cvs ::state/id => ::state/cvs | #(not= id (:id (selected-id cv-state)))]
  (let [next (update-in cv-state [:docs] dissoc id)]
    (if (= (:id (selected-id next)) id)
      (assoc next :selected nil)
      next)))

(>defn active-cv-path
  "Gives the path (suitable for `get-in` and co.) to the currently
  selected CV."
  ([cv-state]
   [::state/cvs => (? coll?)]
   (when-let [sel (selected-id cv-state)]
     [:docs sel]))
  ([cv-state & extra-paths]
   [::state/cvs (s/coll-of string?) => coll?]
   (concat (active-cv-path cv-state) extra-paths)))

(>defn does-exist?
  "Does the passed in CV id exist in docs?"
  [cv-state id]
  [::state/cvs ::state/id => boolean?]
  (contains? (:docs cv-state) id))

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
