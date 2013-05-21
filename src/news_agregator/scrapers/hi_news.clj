(ns parse-enlive-html.scrapers.hi-news
  (:require [net.cgrand.enlive-html :as html]
            [parse-enlive-html.utils :as utils]))

; hi-news.ru

(def *article-selector* [:div.roll.main-roll [:div.type-post (html/but :.large)]])

(def *headline-selector* [html/root :h2 :a])

(def *summary-selector* [html/root :div.text [:p (html/nth-of-type 2)]])

(defn scrape-articles [site-address]
  (html/select (utils/fetch-url site-address "utf-8") *article-selector*))

(defn extract-article [node]
  (let [headline-node (first (html/select [node] *headline-selector*))
        headline (-> headline-node
                     html/text
                     (utils/remove-from-start "|"))
        link     (->> headline-node
                      :attrs
                      :href)
        summary  (first (html/select [node] *summary-selector*))
        result   (map html/text [link headline summary])]
    (zipmap [:link :headline :summary] result)))
