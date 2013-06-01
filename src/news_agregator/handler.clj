(ns news_agregator.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.contrib.math :as math]
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

(def menu-items [{:text "Информационные технологии" :href "/articles/it/1"}
                 {:text "Экономика" :href "/articles/economics/1"}
                 {:text "Спорт" :href "/articles/sport/1"}
                 {:text "Разное" :href "/articles/different/1"}
                 {:text "Все" :href "/articles/all/1"}])

; ***************** print *****************

(defn get-new-articles []
  (doseq [info site-info]
    (doseq [article (utils/get-articles info)]
      (let [new-article (utils/add-hashcode article)]
        (if-not (model/article-exists? new-article)
          (model/add-article new-article)
          ;(utils/print-article new-article)
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

(def articles-per-page 10)

(defn calc-offset [page-num limit]
  (let [page (- page-num 1)]
      (* page limit)))

(defn layout-page [articles-type page-num]
  (let [page (Integer. page-num)
        limit  articles-per-page
        offset (calc-offset page limit)
        pages-count (-> (model/get-articles articles-type)
                        count
                        (/ articles-per-page)
                        math/ceil
                        int)]
    (view/layout menu-items
                 articles-type
                 (model/get-articles articles-type limit offset)
                 page
                 pages-count)))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/articles/all/1"))
  (GET "/articles/:type/:page" [type page] (layout-page type page))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (do (run-articles-fetching)
      (handler/site app-routes)))