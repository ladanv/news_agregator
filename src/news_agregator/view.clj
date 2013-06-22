(ns news_agregator.view
  (:require [hiccup.core :refer :all]
            [clojure.contrib.math :as math]))

(def pages-limit 10)

(def header "Агрегатор новостей")

(defn leftnav [menu-items]
  [:div#leftnav
   (for [{:keys [href text]} menu-items]
     [:p [:a {:href href} text]])])

(defn pager [link-href
             curr-page
             count
             limit]
  [:div.pager
   (if-not (= curr-page 1)
     [:a.prev-page {:href (str link-href "/" (- curr-page 1))} "Предыдущая"])
   (if-not (= curr-page count)
     [:a.next-page {:href (str link-href "/" (+ curr-page 1))} "Следующая"])
   [:div.pages
    (let [pages-limit (min count limit)
          half-limit (-> pages-limit
                         (/ 2)
                         math/ceil
                         int)
          left-offset1 (- curr-page half-limit)
          left-offset2 (+ (- count pages-limit) 1)
          left-offset (min left-offset1 left-offset2)
          begin (max 1 left-offset)
          end (+ begin pages-limit)]
      (for [page (range begin end)]
        (if (= curr-page page)
          [:a.curr-page page]
          [:a.page {:href (str link-href "/" page)} page])))]])

(defn articles [articles-list]
  [:div#content
   (for [{:keys [headline link summary]} articles-list]
     [:div.article
      [:h2 [:a {:href link :target "newtab"} headline]]
      [:p summary]])])

(defn layout [menu-items
             articles-type
             articles-list
             curr-page
             pages-count]
  (let [link-href (str "/articles/" articles-type)]
    (html [:html
           [:head
            [:meta {:http-equiv "Content-Type" :content "text/html; charset=utf-8"}]
            [:title {:id "title"} header]
            [:link {:rel "stylesheet" :type "text/css" :href "/css/main.css"}]]
           [:body
            [:div#container
             [:div#header
              [:h1 header]]
             (leftnav menu-items)
             (pager link-href curr-page pages-count pages-limit)
             (articles articles-list)
             (pager link-href curr-page pages-count pages-limit)
             [:div#footer "Copyright © 2013 - Vitaliy Ladan"]]]])))
