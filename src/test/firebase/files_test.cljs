(ns test.firebase.files-test
  (:require [app.model.state :as state]
            [app.model.cv :as cv]
            [cljs.test :include-macros true :refer [deftest is]]))

(deftest is-valid?-test
  (let [rand-id (random-uuid)]
    (is (cv/is-valid? (-> (state/initial-state)
                          :cvs
                          (cv/select rand-id))))
    ;; selected won't have a value here
    (is (not (cv/is-valid? (-> (state/initial-state)
                               :cvs))))))
