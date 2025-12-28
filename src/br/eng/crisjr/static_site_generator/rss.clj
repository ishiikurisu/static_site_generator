(ns br.eng.crisjr.static-site-generator.rss
  (:require [clojure.string :as str]
            [br.eng.crisjr.commons.utils :as utils])
  (:import  java.time.format.DateTimeFormatter
            java.time.ZonedDateTime
            java.time.ZoneId))

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

(defn to-rfc-1123 [timestamp]
  (let [formatter (.withZone DateTimeFormatter/ISO_INSTANT (ZoneId/systemDefault))
        zoned-date-time (ZonedDateTime/parse timestamp formatter)]
    (.format DateTimeFormatter/RFC_1123_DATE_TIME zoned-date-time)))

(defn- render-rss-entry [note]
  (str "<item>"
       "<title>" (get note "title") "</title>"
       "<link>" (get note "path") "</link>"
       "<description>" (get note "content") "</description>"
       "<pubDate>" (to-rfc-1123 (get note "last_updated_date")) "</pubDate>"
       "</item>"))

(defn- build-feed [url notes]
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
       "<rss version=\"2.0\">"
       "<channel>"
       "<title>Cris Silva Jr.'s Notes</title>"
       "<link>" url "</link>"
       "<description>Notes from my personal digital garden</description>"
       "<lastBuildDate>"
         (-> (.toString (ZonedDateTime/now))
             (str/split #"\[")
             first
             to-rfc-1123)
       "</lastBuildDate>"
       (->> (map render-rss-entry notes)
            (reduce str ""))
       "</channel>"
       "</rss>"))

(defn generate [input-dir url output-path]
  (let [index (utils/load-index input-dir)
        notes (mapv #(load-note input-dir url %) index)]
    (->> (build-feed url notes)
         (spit output-path))))

