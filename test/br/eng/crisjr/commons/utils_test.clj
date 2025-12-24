(ns br.eng.crisjr.commons.utils-test
  (:require [clojure.test :refer :all]
            [br.eng.crisjr.commons.utils :as utils]))

(deftest test-render-csv
  (testing "render csv"
    (let [input-csv (str "name,age\n"
                         "Mira,27\n"
                         "Pietro,24\n"
                         ",\n")
          expected-html (str "<table>"
                             "<tr><th>name</th><th>age</th></tr>"
                             "<tr><td>Mira</td><td>27</td></tr>"
                             "<tr><td>Pietro</td><td>24</td></tr>"
                             "</table>")
          obtained-html (utils/render-csv input-csv)]
      (is (= expected-html obtained-html)))))

(deftest test-get-new-path
  (testing "expected file extensions"
    (is (= "file.html" (utils/get-new-path "file.md")))
    (is (= "file.html" (utils/get-new-path "file.csv")))
    (is (= "file.html" (utils/get-new-path "file.html")))
    (is (= "file.json" (utils/get-new-path "file.json"))))
  (testing "unexpected file extensions"
    (is (= "file.txt" (utils/get-new-path "file.txt")))
    (is (= "file.png" (utils/get-new-path "file.png")))
    (is (= "file.jpg" (utils/get-new-path "file.jpg")))))

