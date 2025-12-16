(ns br.eng.crisjr.static-site-generator
  (:gen-class)
  (:require [br.eng.crisjr.commons.command-line-arguments :as cli]
            [br.eng.crisjr.static-site-generator.wiki :as wiki]))

; args:
;     -i input repository
;     -t templates directory
;     -o output directory
(defn- run-wiki [args]
  (let [input-repository (get args "-i")
        templates-directory (get args "-t")
        output-directory (get args "-o" ".")]
    (cond
      (nil? input-repository)
        (println "Error: no input repository set")
      (nil? templates-directory)
        (println "Error: no templates directory set")
      :else
        (wiki/generate input-repository
                       templates-directory
                       output-directory))))

(defn -main [& argv]
  (let [args (cli/parse argv)
        tool (get args "tool" "")]
    (cond
      (= tool "wiki") (run-wiki args)
      :else (println (str "Unknown command " tool)))))

