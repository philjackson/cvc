(ns app.model.cv
  (:require [cljs.spec.alpha :as s]
            [ghostwheel.core :as g
             :refer [>defn => | ?]]))

(>defn active-cv-path
  ([state]
   [any? => coll?]
   [:data])
  ([state & extra-paths]
   [any? (s/coll-of string?) => coll?]
   (concat (active-cv-path state) extra-paths)))
