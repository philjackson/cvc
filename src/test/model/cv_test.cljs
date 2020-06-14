(ns test.model.cv-test
  (:require [app.model.state :as state :refer [initial-state]]
            [cljs.spec.alpha :as s]
            [app.model.cv :as cv]
            [ghostwheel.core :as g]
            [cljs.test :include-macros true :refer [deftest is run-tests testing are]]))

(deftest select-test
  (let [rand-id (random-uuid)]
    (is (= (cv/select (initial-state) rand-id)
           {:cvs {:docs {} :selected rand-id}
            :route-match nil
            :user nil
            :config {}}))))

(deftest selected-test
  (is (nil? (cv/selected (initial-state))))

  (let [rand-id (random-uuid)]
    (is (= (cv/selected (cv/select (initial-state) rand-id)) rand-id))))

(deftest add-cv-test
  (let [rand-id (random-uuid)]
    (is (= (-> (initial-state)
               (cv/add {:id rand-id}))
           {:cvs {:docs {rand-id {:id rand-id}}
                  :selected nil}
            :route-match nil
            :user nil
            :config {}}))))

(deftest active-cv-path
  (let [rand-id (random-uuid)]
    (is (= (-> (initial-state)
               (cv/add {:id rand-id})
               (cv/select rand-id)
               (cv/active-cv-path))
           [:cvs :docs rand-id]))))
