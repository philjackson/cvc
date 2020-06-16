(ns test.model.state-test
  (:require [app.model.state :as state]
            [clojure.spec.gen.alpha :as gen]
            [cljs.spec.alpha :as s]
            [cljs.test :include-macros true :refer [deftest is run-tests testing are]]))

(defn gen-gen [thing]
  (gen/generate (s/gen thing)))

(deftest check-specs
  (testing "::state/id"
    (testing "valid values"
      (are [value] (s/valid? ::state/id value)
        #uuid "188923e8-fcda-4315-a40a-43e402b986f5"
        #uuid "8fdd5a89-8c5d-4efb-bd9e-570622d669a2"))

    (testing "invalid values"
      (are [value] (not (s/valid? ::state/id value))
        nil
        1
        {:one 1}
        :one
        []
        [1 2 3])))

  (testing "::state/cv"
    (testing "valid values"
      (are [value] (s/valid? ::state/cv value)
        {:id (gen-gen ::state/id)
         :name (gen-gen ::state/name)}))

    (testing "invalid values"
      (are [value] (not (s/valid? ::state/cv value))
        {:id nil}
        nil
        "hi")))

  (testing "::state/docs"
    (testing "valid values"
      (are [value] (s/valid? ::state/docs value)
        {}
        {(gen-gen ::state/id) (gen-gen ::state/cv)}))

    (testing "invalid values"
      (are [value] (not (s/valid? ::state/docs value))
        {:id nil}
        (gen-gen ::state/cv)
        "hi")))

  (testing "::state/cvs"
    (testing "valid values"
      (are [value] (s/valid? ::state/cvs value)
        {:selected (gen-gen ::state/id)
         :docs (gen-gen ::state/docs)}))

    (testing "invalid values"
      (are [value] (not (s/valid? ::state/cvs value))
        nil
        {:selected (gen-gen ::state/id)})))

  (testing "::state/route-match"
    (testing "valid values"
      (are [value] (s/valid? ::state/route-match value)
        @state/match
        nil
        {}))

    (testing "invalid values"
      (are [value] (not (s/valid? ::state/route-match value))
        1
        [])))

  (testing "::state/user"
    (testing "valid values"
      (are [value] (s/valid? ::state/user value)
        @state/user
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
        [1 2 3])))

  (testing "::state/state"
    (testing "valid values"
      (are [value] (s/valid? ::state/state value)
        @state/all-seeing-state
        {:config {}
         :cvs (gen-gen ::state/cvs)
         :route-match {}
         :user {}}))

    (testing "invalid values"
      (are [value] (not (s/valid? ::state/state value))
        nil
        {}
        []
        1
        {:one 1}
        :one
        {:user {:uid "hi" :email "an@email.com" :display-name "Phil Jackson"}}
        [1 2 3]))))
