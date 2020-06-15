(ns test.model.cv-test
  (:require [app.model.state :as state :refer [initial-state]]
            [cljs.spec.alpha :as s]
            [app.model.cv :as cv]
            [ghostwheel.core :as g]
            [cljs.test :include-macros true :refer [deftest is run-tests testing are]]))

(deftest select-test
  (let [rand-id (random-uuid)]
    (is (= (-> (initial-state)
               :cvs
               (cv/select rand-id))
           {:docs {} :selected rand-id}))))

(deftest selected-test
  (is (nil? (-> (initial-state)
                :cvs
                cv/selected)))

  (let [rand-id (random-uuid)]
    (is (= (-> (initial-state)
               :cvs
               (cv/select rand-id)
               cv/selected)
           rand-id))))

(deftest add-cv-test
  (let [rand-id (random-uuid)]
    (is (= (-> (initial-state)
               :cvs
               (cv/add {:id rand-id}))
           {:docs {rand-id {:id rand-id}}
            :selected nil}))))

(deftest active-cv-path
  (let [rand-id (random-uuid)]
    (is (nil? (-> (initial-state)
                  :cvs
                  cv/active-cv-path)))
    (is (= (-> (initial-state)
               :cvs
               (cv/add {:id rand-id})
               (cv/select rand-id)
               cv/active-cv-path)
           [:docs rand-id]))))