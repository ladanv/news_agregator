(ns parse-enlive-html.scrapers.newsru
  (:require [net.cgrand.enlive-html :as html]
            [parse-enlive-html.utils :as utils]))

; "http://hitech.newsru.com"

(def site-url "http://hitech.newsru.com")

(def *article-selector* [:tr.article])

(def *headline-selector* [html/root :> :td :.articletitle])

(def *summary-selector* [html/root :> :td :div :.articletext])

(defn scrape-articles [site-address]
  (html/select (utils/fetch-url site-address "windows-1251") *article-selector*))

(defn extract-article [node]
  (let [headline (first (html/select [node] *headline-selector*))
        link     (str site-url (:href (:attrs headline)))
        summary  (first (html/select [node] *summary-selector*))
        result   (map html/text [link headline summary])]
    (zipmap [:link :headline :summary] result)))