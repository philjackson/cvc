(ns test.model.cv-test
  (:require [app.model.state :as state :refer [initial-state]]
            [app.model.cv :as cv]
            [test.helpers :refer [gen-gen]]
            [cljs.test :include-macros true :refer [deftest is]]))

(deftest select-test
  (let [rand-id (random-uuid)]
    (is (= (-> (initial-state)
               :cvs
               (cv/select rand-id))
           {:docs {} :selected rand-id}))))

(deftest selected-id-test
  (is (nil? (-> (initial-state)
                :cvs
                cv/selected-id)))

  (let [rand-id (random-uuid)]
    (is (= (-> (initial-state)
               :cvs
               (cv/select rand-id)
               cv/selected-id)
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

(deftest public-cvs-test
  (let [one (random-uuid)
        two (random-uuid)
        three (random-uuid)
        ;; two CVs with public IDs, one without
        cv-state (-> (initial-state)
                     :cvs
                     (cv/add {:id one
                              :public? true
                              :public-id (random-uuid)
                              :name (gen-gen ::state/name)})
                     (cv/add {:id two
                              :public? true
                              :public-id (random-uuid)
                              :name (gen-gen ::state/name)})
                     (cv/add {:id three
                              :name (gen-gen ::state/name)}))
        public (cv/public-cvs cv-state)]
    (is (= 2 (count public)))
    (is (= [one two]
           (map :id public)))))
