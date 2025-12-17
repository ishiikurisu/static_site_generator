(ns br.eng.crisjr.commons.utils
  (:require [clojure.data.json :as json]))

(defn load-index [pwd]
  (-> (str pwd "/index.blog.json")
      slurp
      json/read-str))

(defn load-templates [pwd]
  {:index (-> (str pwd "/index.template.html")
              slurp)
   :index-post (-> (str pwd "/index.post.template.html")
                   slurp)
   :post (-> (str pwd "/post.template.html")
             slurp)})

(defn load-post [pwd path]
  (-> (str pwd "/" path)
      slurp))

(defn render-md [from]
  ;; TODO complete me!
  from)

(defn render-csv [from]
  ;; TODO complete me!
  from)

