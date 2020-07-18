(ns lgdsync.core-test
  (:require [clojure.spec.test.alpha :as st]
            [clojure.test :refer :all]
            [lgdsync.core :as core]))

(st/unstrument)
(st/instrument)

(deftest parse-cmd-line-args
  (testing "Without options"
    (let [input ["/foo" "/bar"]
          actual (#'core/parse-cmd-line-args input)
          expected {:args ["/foo" "/bar"]
                    :opts {}}]
      (is (= actual expected))))
  (testing "Without option 1"
    (let [input ["/foo" "/bar" "--profile=buz"]
          actual (#'core/parse-cmd-line-args input)
          expected {:args ["/foo" "/bar"]
                    :opts {"profile" "buz"}}]
      (is (= actual expected))))
  (testing "Without option 2"
    (let [input ["--profile=buz" "/foo" "/bar"]
          actual (#'core/parse-cmd-line-args input)
          expected {:args ["/foo" "/bar"]
                    :opts {"profile" "buz"}}]
      (is (= actual expected))))
  (testing "Without option 2"
    (let [input ["--profile=buz" "--flag" "/foo" "/bar"]
          actual (#'core/parse-cmd-line-args input)
          expected {:args ["/foo" "/bar"]
                    :opts {"profile" "buz"
                           "flag" true}}]
      (is (= actual expected)))))
