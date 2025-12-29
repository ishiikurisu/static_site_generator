(ns br.eng.crisjr.static-site-generator.wiki
  (:require [clojure.string :as str]
            [br.bsb.liberdade.strint :refer [strint]]
            [br.eng.crisjr.commons.utils :as utils]))

(def lang-tags {nil     ""
                "pt-br" "🇧🇷"
                "en"    "🇬🇧"
                "ja"    "🇯🇵"})

(defn- populate-template-content [template content]
  (strint template {"content" content}))

(defn- generate-note-content [template input-dir note]
  (let [path (get note "path")
        post (utils/load-post input-dir path)
        render-fn (utils/get-render-fn path)]
    (when-not (str/ends-with? path ".geojson")  ; HACK avoid geojson for now
      (->> (render-fn post)
           (populate-template-content template)))))

(defn- render-note [template note input-dir output-dir]
  (try
    (do
      (spit (str output-dir "/" (-> (get note "path")
                                    utils/get-new-path))
            (generate-note-content template input-dir note))
      true)
    (catch Exception e
      (do
        (println e)
        false))))

(defn render-notes
  [index templates input-dir output-dir]
  (reduce (fn [acc note]
            (if (render-note (get templates :post)
                             note
                             input-dir
                             output-dir)
              (conj acc note)
              acc))
          []
          index))

(defn- populate-index-post-template [template note]
  (strint template
          {"path" (utils/get-new-path (get note "path"))
           "title" (get note "title")
           "description" (get note "description")
           "dateLabel" (utils/build-date-label note)
           "language" (->> (get note "lang")
                           (get lang-tags))}))

(defn- build-index-contents [template notes]
  (->> (map #(populate-index-post-template template %) notes)
       (reduce str "")))

(defn render-index [index templates notes output-directory]
  (let [index-contents (build-index-contents (:index-post templates)
                                             notes)]
    (spit (str output-directory "/index.html")
          (populate-template-content (:index templates)
                                     index-contents))))

(defn generate [input-repository templates-repository output-directory]
  (let [index (utils/load-index input-repository)
        templates (utils/load-templates templates-repository)
        rendered-notes (render-notes index
                                     templates
                                     input-repository
                                     output-directory)
        rendered-index (render-index index
                                     templates
                                     rendered-notes
                                     output-directory)]
    nil))

