(ns news_agregator.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [ring.util.response :as resp]
            (news_agregator [utils :as utils]
                            [model :as model]
                            [view  :as view])
            (news_agregator.scrapers [newsru :as newsru]
                                     [inopressa :as inopressa]
                                     [hi-news :as hi-news])
            [overtone.at-at :as at-at]))

;; article types

(def it "it")
(def politics "politics")
(def economics "economics")
(def different "different")
(def sport "sport")

(def site-info (list {:address "http://hi-news.ru"
                      :type it
                      :scrape-fn hi-news/scrape-articles
                      :extract-fn hi-news/extract-article}
                     {:address "http://hitech.newsru.com"
                      :type it
                      :scrape-fn newsru/scrape-articles
                      :extract-fn newsru/extract-article}
                     {:address "http://www.inopressa.ru/rubrics/different"
                      :type different
                      :scrape-fn inopressa/scrape-articles
                      :extract-fn inopressa/extract-article}
                     {:address "http://www.inopressa.ru/rubrics/economics"
                      :type economics
                      :scrape-fn inopressa/scrape-articles
                      :extract-fn inopressa/extract-article}
                     {:address "http://www.inopressa.ru/rubrics/sport"
                      :type sport
                      :scrape-fn inopressa/scrape-articles
                      :extract-fn inopressa/extract-article}))

(def menu-items [{:text "Информационные технологии" :href "/articles/it"}
                 {:text "Политика" :href "/articles/politics"}
                 {:text "Экономика" :href "/articles/economics"}
                 {:text "Спорт" :href "/articles/sport"}
                 {:text "Разное" :href "/articles/different"}
                 {:text "Все" :href "/articles/all"}])

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

(defn run-articles-fetching []
  (at-at/every fetching-period get-new-articles my-pool :fixed-delay true))

;; (get-new-articles)
;; (run-articles-fetching)
;; (at-at/show-schedule my-pool)
;; (at-at/stop-and-reset-pool! my-pool :strategy :kill)

(defn layout-page [type limit offset]
  (if (= type "all")
    (view/layout menu-items (model/get-articles limit offset))
    (view/layout menu-items (model/get-articles type limit offset))))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/articles/all"))
  (GET "/articles/:type" [type] (layout-page type 10 0))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (do ;(run-articles-fetching)
      (handler/site app-routes)))