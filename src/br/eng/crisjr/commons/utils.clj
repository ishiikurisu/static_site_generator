(ns br.eng.crisjr.commons.utils
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [br.bsb.liberdade.strint :refer [strint]]
            [markdown.core :refer [md-to-html-string]]))

(defn load-index [pwd]
  (-> (str pwd "/index.blog.json")
      slurp
      json/read-str))

(defn- maybe-slurp [path]
  (try
    (slurp path)
    (catch Exception e
      nil)))

(defn load-templates [pwd]
  {:index (-> (str pwd "/index.template.html")
              slurp)
   :index-post (-> (str pwd "/index.post.template.html")
                   slurp)
   :post (-> (str pwd "/post.template.html")
             maybe-slurp)})

(defn load-post [pwd path]
  (-> (str pwd "/" path)
      slurp))

(def render-md md-to-html-string)

(defn- csv-row-to-html [row tag]
  (str "<tr>"
       (->> (map #(strint "<%{tag}>%{it}</%{tag}>"
                          {"tag" tag
                           "it" %})
                 row)
            (apply str))
       "</tr>"))

(defn render-csv [from]
  (let [table (->> (str/split from #"\n")
                   (map #(str/split % #",")))]
    (loop [head (first table)
           tail (rest table)
           outlet "<table>"
           first-line? true]
      (if (nil? head)
        (str outlet "</table>")
        (recur (first tail)
               (rest tail)
               (if (-> head count zero?)
                  outlet
                  (str outlet
                       (csv-row-to-html head
                                        (if first-line?
                                          "th"
                                          "td"))))
               false)))))

(defn get-render-fn [path]
  (cond
    (str/ends-with? path ".md") render-md
    (str/ends-with? path ".csv") render-csv 
    :else identity))

(defn get-new-path [from]
  (str/replace from #"\.md|\.csv$" ".html"))

(defn spy [it]
  (clojure.pprint/pprint it)
  it)

