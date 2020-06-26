(ns test.helpers
  (:require [clojure.spec.gen.alpha :as gen]
            [cljs.spec.alpha :as s]))

(defn gen-gen [thing]
  (gen/generate (s/gen thing)))
