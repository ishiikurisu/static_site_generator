(ns br.eng.crisjr.static-site-generator
  (:gen-class)
  (:require [br.eng.crisjr.commons.command-line-arguments :as cli]))

(defn -main
  "I don't do a whole lot ... yet."
  [& argv]
  (let [args (cli/parse argv)]
    (println args)))

