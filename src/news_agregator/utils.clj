(ns parse-enlive-html.utils
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str])
  (:import [java.lang.String]))

(defn encode-string [s encoding-system]
  (-> s
      .getContent
      (java.io.InputStreamReader. encoding-system)))

(defn fetch-url [url encoding-system]
  (html/html-resource (-> url
                          java.net.URL.
                          (encode-string encoding-system))))

(defn remove-from-start [s sub]
  (let [pos (.indexOf s sub)]
    (if (> pos 0)
        (str/trim (subs s (+ pos 1) (count s)))
      s)))

(defn article-type-to-int [t]
  (cond
   (= :it t) 1
   (= :different t) 2
   (= :economics t) 3
   (= :sport t) 4))

(defn get-articles [info]
  (let [site-address (:address info)
        article-type (:article-type info)
        scrape-fn    (:scrape-fn info)
        extract-fn   (:extract-fn info)
        process-node (fn [node]
                       (-> node
                           extract-fn
                           (assoc :article_types_id (article-type-to-int article-type))))]
    (map process-node (scrape-fn site-address))))

(defn add-hashcode [article]
  (assoc article :hashcode (->> article
                                :link
                                hash
                                )))