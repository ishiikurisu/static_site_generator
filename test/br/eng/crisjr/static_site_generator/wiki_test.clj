(ns br.eng.crisjr.static-site-generator.wiki-test
  (:require [clojure.test :refer :all]
            [br.eng.crisjr.static-site-generator.wiki :as wiki]
            ))

(deftest test-get-new-path
  (testing "expected file extensions"
    (is (= "file.html" (wiki/get-new-path "file.md")))
    (is (= "file.html" (wiki/get-new-path "file.csv")))
    (is (= "file.html" (wiki/get-new-path "file.html")))
    (is (= "file.json" (wiki/get-new-path "file.json"))))
  (testing "unexpected file extensions"
    (is (= "file.txt" (wiki/get-new-path "file.txt")))
    (is (= "file.png" (wiki/get-new-path "file.png")))
    (is (= "file.jpg" (wiki/get-new-path "file.jpg")))))

