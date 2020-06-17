(ns test.model.cv-test
  (:require [app.model.state :as state :refer [initial-state]]
            [app.model.cv :as cv]
            [cljs.test :include-macros true :refer [deftest is]]))

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
               (cv/add {:id rand-id :name "Main"}))
           {:docs {rand-id {:id rand-id :name "Main"}}
            :selected nil}))))

(deftest active-cv-path
  (let [rand-id (random-uuid)]
    (is (nil? (-> (initial-state)
                  :cvs
                  cv/active-cv-path)))
    (is (= (-> (initial-state)
               :cvs
               (cv/add {:id rand-id
                        :name "Main"})
               (cv/select rand-id)
               cv/active-cv-path)
           [:docs rand-id]))))

(deftest does-exist?-test
  (let [rand-id (random-uuid)]
    (is (false? (-> (initial-state)
                    :cvs
                    (cv/does-exist? rand-id))))
    (is (true? (-> (initial-state)
                   :cvs
                   (cv/add {:id rand-id
                            :name "Main"})
                   (cv/select rand-id)
                   (cv/does-exist? rand-id))))))
