(ns app.truthy)

(defn truthy? [st]
  (and (not (nil? st))
       (> (count st) 0)))
