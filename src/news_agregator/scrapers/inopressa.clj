(ns news_agregator.scrapers.inopressa
  (:require [net.cgrand.enlive-html :as html]
            [news_agregator.utils :as utils]))

; "http://www.inopressa.ru/rubrics/different"

(def site-url "http://www.inopressa.ru/rubrics/different")

(def *article-selector* [:div.topic])

(def *headline-selector* [html/root :> :h2 :a])

(def *summary-selector* [html/root :> :div :a])

(defn scrape-articles [site-address]
  (html/select (utils/fetch-url site-address "windows-1251") *article-selector*))

(defn extract-article [node]
  (let [headline (first (html/select [node] *headline-selector*))
        link     (str site-url (:href (:attrs headline)))
        summary  (first (html/select [node] *summary-selector*))
        result   (map html/text [link headline summary])]
    (zipmap [:link :headline :summary] result)))