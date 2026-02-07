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

(def wiki-help (str "# wiki: renders each file separately\n\n"
                    "-i input directory\n"
                    "-t template directory\n"
                    "-o output directory (default: \".\")\n"
                    "\n"))

; args:
;     -i input repository 
;     -o output file (default: "./feed.rss")
;     -h feed title (default: "RSS Feed")
;     -d description (default: "")
;     -u url
(defn- run-rss [args]
  (let [input-repository (get args "-i")
        output-file (get args "-o" "./feed.rss")
        feed-title (get args "-h" "RSS Feed")
        description (get args "-d" "")
        url (get args "-u")]
    (cond
      (nil? input-repository) (println "Error: no input repository set")
      (nil? url) (println "Error: no URL set")
      :else (rss/generate input-repository
                          feed-title
                          description
                          url
                          output-file))))

(def rss-help (str "# rss: generates an RSS file\n\n"
                   "-i input repository\n"
                   "-u reference URL\n"
                   "-h RSS feed title (default: \"RSS Feed\")\n"
                   "-d Description (default: \"\")\n"
                   "-o RSS feed file (default: \"./feed.rss\")\n"
                   "\n"))

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

(def microblog-help (str "# microblog: renders all notes in a single file\n\n"
                         "-i input repository\n"
                         "-t templates directory\n"
                         "-o output file (default: \"./index.html\")\n"
                         "\n"))

; help
;    no args accepted
(defn- run-help [args]
  (let [tools [wiki-help
               rss-help
               microblog-help]]
    (println (reduce str "" tools))))

(defn -main [& argv]
  (let [args (cli/parse argv)
        tool (get args "tool" "")]
    (condp = tool
      "wiki" (run-wiki args)
      "rss" (run-rss args)
      "microblog" (run-microblog args)
      (run-help args))))

