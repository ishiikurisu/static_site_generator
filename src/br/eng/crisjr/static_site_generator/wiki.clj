(ns br.eng.crisjr.static-site-generator.wiki
  (:require [clojure.string :as str]
            [br.bsb.liberdade.strint :refer [strint]]
            [br.eng.crisjr.commons.utils :as utils]))

(defn get-new-path [from]
  (str/replace from #"\.md|\.csv$" ".html"))

(defn populate-template [template content]
  (strint template {"content" content}))

(defn generate-content [template input-dir note]
  (let [path (get note "path")
        post (utils/load-post input-dir path)]
    (->> (cond
           (str/ends-with? path ".md") (utils/render-md post)
           (str/ends-with? path ".csv") (utils/render-csv post)
           :else post)
         (populate-template template))))

(defn render-note [template note input-dir output-dir]
  (try
    (do
      (spit (str output-dir "/" (-> note (get "path") get-new-path))
            (generate-content template input-dir note))
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

(defn render-index [index templates notes output-directory]
  ;; TODO complete me!
  )

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

