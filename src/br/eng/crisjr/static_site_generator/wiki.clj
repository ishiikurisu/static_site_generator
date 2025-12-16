(ns br.eng.crisjr.static-site-generator.wiki
  (:require [br.eng.crisjr.commons.utils :as utils]))

(defn get-new-path [from]
  ;; TODO complete me!
  from)

(defn generate-content [template input-dir note]
  ;; TODO complete me!
  "")

(defn render-note [template note input-dir output-dir]
  (try
    (do
      (spit (str output-dir "/" (-> note (get "path") get-new-path))
            (generate-content template input-dir note))
      true)
    (catch Exception e
      false)))

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

