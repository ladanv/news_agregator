(ns news_agregator.utils
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

;; (defn article-type-to-int [t]
;;   (let [type (keyword t)]
;;     (cond
;;      (= :all type) 0
;;      (= :it type) 1
;;      (= :different type) 2
;;      (= :economics type) 3
;;      (= :sport type) 4)))

(defn get-articles [info]
  (let [site-address (:address info)
        article-type (:type info)
        scrape-fn    (:scrape-fn info)
        extract-fn   (:extract-fn info)
        process-node (fn [node]
                       (-> node
                           extract-fn
                           (assoc :type article-type)))]
    (map process-node (scrape-fn site-address))))

(defn add-hashcode [article]
  (assoc article :hashcode (->> article
                                :link
                                hash)))

;; (defn print-article [article]
;;   (model/add-article article)
;;   (println "\n---------------------------------")
;;   (println "\t" (article :link))
;;   (println "\t" (article :headline))
;;   (println "\t" (article :summary))
;;   (println "\t" (article :hashcode))
;;   (println "\t" (article :article_types_id)))