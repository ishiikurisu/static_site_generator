(ns br.eng.crisjr.commons.command-line-arguments-test
  (:require [clojure.test :refer :all]
            [br.eng.crisjr.commons.command-line-arguments :as cli]))

(deftest test-parse-command-line-arguments
  (testing "empty arguments"
    (is (= {} (cli/parse []))))
  (testing "regular arguments"
    (is (= {"tool" "tool"
            "-i" "input_folder"
            "-o" "output_folder"}
           (cli/parse ["tool" "-i" "input_folder" "-o" "output_folder"])))))


