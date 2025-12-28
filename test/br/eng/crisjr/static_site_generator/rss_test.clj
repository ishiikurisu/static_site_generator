(ns br.eng.crisjr.static-site-generator.rss-test
  (:require [clojure.test :refer :all]
            [br.eng.crisjr.static-site-generator.rss :as rss]))

(deftest test-rfc-1123-convertion
  (testing "can format dates to RFC 1123"
    (is (= "Tue, 9 Jun 2020 12:38:33 +0100" (rss/to-rfc-1123 "2020-06-09T11:38:33.000Z")))
    (is (= "Sun, 28 Dec 2025 20:57:05 GMT" (rss/to-rfc-1123 "2025-12-28T20:57:05.542939Z")))))

