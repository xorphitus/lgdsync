(ns lgdsync.core-test
  (:require [clojure.spec.test.alpha :as st]
            [clojure.test :refer :all]
            [lgdsync.core :as core]))

(st/unstrument)
(st/instrument)

(deftest validate-args
  (testing "Without options"
    (let [input ["/foo" "/bar"]
          actual (#'core/validate-args input)
          expected {:src "/foo"
                    :dest "/bar"
                    :options {:profile "default"}}]
      (is (= actual expected))))
  (testing "Without option 1"
    (let [input ["/foo" "/bar" "--profile=buz"]
          actual (#'core/validate-args input)
          expected {:src "/foo"
                    :dest "/bar"
                    :options {:profile "buz"}}]
      (is (= actual expected))))
  (testing "Without option 2"
    (let [input ["--profile=buz" "/foo" "/bar"]
          actual (#'core/validate-args input)
          expected {:src "/foo"
                    :dest "/bar"
                    :options {:profile "buz"}}]
      (is (= actual expected)))))
