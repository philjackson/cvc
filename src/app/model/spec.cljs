(ns app.model.spec
  (:require ))

(def non-empty-string-alphanumeric
  "Generator for non-empty alphanumeric strings"
  (gen/such-that #(not= "" %)
                 (gen/string-alphanumeric)))

(def email-gen
  "Generator for email addresses"
  (gen/fmap
   (fn [[name host tld]]
     (str name "@" host "." tld))
   (gen/tuple
    non-empty-string-alphanumeric
    non-empty-string-alphanumeric
    non-empty-string-alphanumeric)))
