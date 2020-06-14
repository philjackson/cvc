(ns app.model.cv
  (:require [app.model.state :as state]
            [app.debug :refer [debug?]]
            [cljs.spec.alpha :as s]
            [ghostwheel.core :as g
             :refer [>defn =>]]))

(>defn select
  [state id]
  [::state/state ::state/id => ::state/state]
  (assoc-in state [:cvs :selected] id))

(>defn add
  [state cv]
  [::state/state ::state/cv => ::state/state]
  (assoc-in state [:cvs :docs (:id cv)] cv))

(>defn selected
  [state]
  [::state/state => (s/nilable ::state/id)]
  (-> state
      :cvs
      :selected))

(>defn active-cv-path
  ([state]
   [::state/state => (s/nilable coll?)]
   (when-let [sel (selected state)]
     [:cvs :docs (selected state)]))
  ([state & extra-paths]
   [::state/state (s/coll-of string?) => coll?]
   (concat (active-cv-path state) extra-paths)))

(when debug? (g/check))
