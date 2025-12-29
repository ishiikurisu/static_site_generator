(ns br.eng.crisjr.static-site-generator.microblog
  (:require [clojure.string :as str]
            [br.bsb.liberdade.strint :refer [strint]]
            [br.eng.crisjr.commons.utils :as utils]))

(defn- render-entry [entry input-repository template]
  (let [path (get entry "path")]
    (strint template
            {"title" (get entry "title")
             "content" (apply (utils/get-render-fn path)
                              [(utils/load-post input-repository path)])})))

(defn generate [input-repository templates-directory output-file]
  (let [index (utils/load-index input-repository)
        templates (utils/load-templates templates-directory)
        {index-template :index
         index-post-template :index-post} templates]
    (spit output-file
          (strint index-template
                  {"content" (->> (map #(render-entry %
                                                      input-repository
                                                      index-post-template)
                                       index)
                                  (reduce str ""))}))))

