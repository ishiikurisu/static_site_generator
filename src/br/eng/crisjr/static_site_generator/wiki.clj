(ns br.eng.crisjr.static-site-generator.wiki
  (:require [br.eng.crisjr.commons.utils :as utils]))

(defn render-note [index templates note output-directory]
  ;; TODO complete me!
  )

(defn render-notes [index templates output-directory]
  ;; TODO complete me!
  )

(defn render-index [index templates notes output-directory]
  ;; TODO complete me!
  )

(defn generate [input-repository templates-repository output-directory]
  (let [index (utils/load-index input-repository)
        templates (utils/load-templates templates-repository)
        rendered-notes (render-notes index templates output-directory)
        rendered-index (render-index index
                                     templates
                                     rendered-notes
                                     output-directory)]
    nil))

