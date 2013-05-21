(ns news_agregator.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            (news_agregator [utils :as utils]
                            [model :as model])
            (news_agregator.scrapers [newsru :as newsru]
                                     [inopressa :as inopressa]
                                     [hi-news :as hi-news])
            [overtone.at-at :as at-at]))

(def site-info (list {:address "http://hi-news.ru"
                      :article-type :it
                      :scrape-fn hi-news/scrape-articles
                      :extract-fn hi-news/extract-article}
                     {:address "http://hitech.newsru.com"
                      :article-type :it
                      :scrape-fn newsru/scrape-articles
                      :extract-fn newsru/extract-article}
                     {:address "http://www.inopressa.ru/rubrics/different"
                      :article-type :different
                      :scrape-fn inopressa/scrape-articles
                      :extract-fn inopressa/extract-article}
                     {:address "http://www.inopressa.ru/rubrics/economics"
                      :article-type :economics
                      :scrape-fn inopressa/scrape-articles
                      :extract-fn inopressa/extract-article}
                     {:address "http://www.inopressa.ru/rubrics/sport"
                      :article-type :sport
                      :scrape-fn inopressa/scrape-articles
                      :extract-fn inopressa/extract-article}))

; ***************** print *****************

(defn print-article [article]
  (model/add-article article)
  (println "\n---------------------------------")
  (println "\t" (article :link))
  (println "\t" (article :headline))
  (println "\t" (article :summary))
  (println "\t" (article :hashcode))
  (println "\t" (article :article_types_id)))

(defn get-new-articles []
  (doseq [info site-info]
    (doseq [article (utils/get-articles info)]
      (let [new-article (utils/add-hashcode article)]
        (if-not (model/article-exists? new-article)
          (model/add-article new-article)
          ;(print-article new-article)
          )))))

(def my-pool (at-at/mk-pool))

(def fetching-period (-> 1000
                         (* 60)
                         (* 15))) ; 15 minutes

(defn main-fn []
  (at-at/every fetching-period get-new-articles my-pool :fixed-delay true))

;; (at-at/show-schedule my-pool)
;; (at-at/stop-and-reset-pool! my-pool :strategy :kill)

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
