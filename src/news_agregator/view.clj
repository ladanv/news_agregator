(ns news_agregator.view
  (:require [hiccup.core :refer :all]
            [clojure.contrib.math :as math]))

;; (defsnippet pager-snip pager-html [:.pager]
;;   [link-href curr-page count limit] 
;;   [:.prev-page] (do-> (if (= curr-page 1)
;;                         (content "")
;;                         (set-attr :href (str link-href "/" (- curr-page 1)))))
;;   [:.next-page] (do-> (if (= curr-page count)
;;                         (content "")
;;                         (set-attr :href (str link-href "/" (+ curr-page 1)))))
;;   [:.page] (let [pages-limit (min count limit)
;;                  half-limit (-> pages-limit
;;                                 (/ 2)
;;                                 math/ceil
;;                                 int)
;;                  left-offset1 (- curr-page half-limit)
;;                  left-offset2 (+ (- count pages-limit) 1)
;;                  left-offset (min left-offset1 left-offset2)
;;                  begin (max 1 left-offset)
;;                  end (+ begin pages-limit)]
;;              (clone-for [page (range begin end)]
;;                         [:a] (do->
;;                               (content (str page))
;;                               (set-attr :href (if (= curr-page page)
;;                                                 ""
;;                                                 (str link-href "/" page)))
;;                               (set-attr :class (if (= curr-page page)
;;                                                  "curr-page"
;;                                                  "page"))))))

(def pages-limit 10)

(def header "Агрегатор новостей")

(defn leftnav [menu-items]
  [:div#leftnav
   (for [item menu-items]
     (let [href (:href item)
           text (:text item)]
       [:p [:a {:href href} text]]))])

(defn pager [link-href
             curr-page
             pages-count
             pages-limit]
  [:div#pager])

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
             [:div#content]
             (pager link-href curr-page pages-count pages-limit)
             [:div#footer "Copyright © 2013 - Vitaliy Ladan"]]]])))
