(ns test.model.state-test
  (:require [app.model.state :as state]
            [cljs.spec.alpha :as s]
            [cljs.test :include-macros true :refer [deftest is run-tests testing are]]))

(deftest check-user-specs
  (testing "::state/id valid values"
    (are [value] (s/valid? ::state/id value)
      "string"))

  (testing "::state/id invalid values"
    (are [value] (not (s/valid? ::state/id value))
      1
      {:one 1}
      :one
      []
      [1 2 3]))

  (testing "::state/user valid values"
    (are [value] (s/valid? ::state/user value)
      {}
      {:uid "hi"
       :email "an@email.com"
       :display-name "Phil Jackson"}))

  (testing "::state/user invalid values"
    (are [value] (not (s/valid? ::state/user value))
      1
      {:one 1}
      :one
      []
      {:uid "hello"}
      {:uid "hello" :email "some@email.com"}
      [1 2 3])))
