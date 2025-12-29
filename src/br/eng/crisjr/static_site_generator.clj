(ns br.eng.crisjr.static-site-generator
  (:gen-class)
  (:require [br.eng.crisjr.commons.command-line-arguments :as cli]
            [br.eng.crisjr.static-site-generator.microblog :as microblog]
            [br.eng.crisjr.static-site-generator.rss :as rss]
            [br.eng.crisjr.static-site-generator.wiki :as wiki]))

; args:
;     -i input repository
;     -t templates directory
;     -o output directory (default: ".")
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

; args:
;     -i input repository 
;     -o output file (default: "./feed.rss")
;     -u url
(defn- run-rss [args]
  (let [input-repository (get args "-i")
        output-file (get args "-o" "./feed.rss")
        url (get args "-u")]
    (cond
      (nil? input-repository) (println "Error: no input repository set")
      (nil? url) (println "Error: no URL set")
      :else (rss/generate input-repository url output-file))))

; args:
;     -i input directory
;     -t templates directory
;     -o output file (default: "/index.html")
(defn- run-microblog [args]
  (let [input-repository (get args "-i")
        templates-directory (get args "-t")
        output-file (get args "-o" "./index.html")]
    (cond
      (nil? input-repository)
        (println "Error: input directory not set")
      (nil? templates-directory)
        (println "Error: templates directory not set")
      :else
        (microblog/generate input-repository
                            templates-directory
                            output-file))))

(defn -main [& argv]
  (let [args (cli/parse argv)
        tool (get args "tool" "")]
    (cond
      (= tool "wiki") (run-wiki args)
      (= tool "rss") (run-rss args)
      (= tool "microblog") (run-microblog args)
      :else (println (str "Unknown command " tool)))))

