(ns test.model.state-test
  (:require [app.model.state :as state]
            [cljs.spec.alpha :as s]
            [cljs.test :include-macros true :refer [deftest is run-tests testing are]]))

(deftest check-specs
  (testing "::state/id"
    (testing "valid values"
      (are [value] (s/valid? ::state/id value)
        "1234567890"
        "hello"))

    (testing "invalid values"
      (are [value] (not (s/valid? ::state/id value))
        nil
        #uuid "b56f61e8-312f-4f7b-8ce6-e0a49b93ed57"
        1
        {:one 1}
        :one
        []
        [1 2 3])))

  (testing "::state/user"
    (testing "valid values"
      (are [value] (s/valid? ::state/user value)
        nil
        {}
        {:uid "hi"
         :email "an@email.com"
         :display-name "Phil Jackson"}))

    (testing "invalid values"
      (are [value] (not (s/valid? ::state/user value))
        1
        {:one 1}
        :one
        []
        {:uid "hello"}
        {:uid "hello" :email "some@email.com"}
        [1 2 3]))))
