(ns br.eng.crisjr.static-site-generator.rss
  (:require [clojure.string :as str]
            [br.eng.crisjr.commons.utils :as utils]))

(defn- get-note-url [url path]
  (str url "/" path))

(defn- adapt-note-contents-for-rss [url inlet]
  (format "<![CDATA[%s]]>"
          (-> inlet
              (str/replace "src=\"./"
                           (str "src=\"" url "/"))
              (str/replace "[" "%#91;")
              (str/replace "]" "%#93;"))))

(defn- load-note [input-dir url note]
  (let [path (get note "path")]
    (assoc note "content" (->> (utils/load-post input-dir path)
                               ((utils/get-render-fn path))
                               (adapt-note-contents-for-rss url))
                "path" (->> (utils/get-new-path path)
                            (get-note-url url)))))

(defn- build-feed [url notes]
  ; TODO complete me!
  (utils/spy notes)
  ""
  )

(defn generate [input-dir url output-path]
  (let [index (utils/load-index input-dir)
        notes (mapv #(load-note input-dir url %) index)]
    (->> (build-feed url notes)
         (spit output-path))))

