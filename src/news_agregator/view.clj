(ns news_agregator.view
  (:require [net.cgrand.enlive-html :refer :all]
            [clojure.contrib.math :as math]))

;; pages

(def layout-html "public/html/layout.html")
(def pager-html "public/html/pager.html")

;; selectors

(def leftnav-sel [:#leftnav])
(def link-sel [leftnav-sel :> first-child])
(def content-sel [:#content])
(def article-sel [[:.article first-of-type]])

;; stippets

(defsnippet link-snip layout-html link-sel
  [{text :text href :href}] 
  [:a] (do-> 
        (content text) 
        (set-attr :href href)))

(defsnippet leftnav-snip layout-html leftnav-sel
  [model data]
  leftnav-sel (content (map model data)))

(defsnippet article-snip layout-html article-sel
  [{:keys [headline link summary]}]
  [:a] (do->
        (content headline)
        (set-attr :href link))
  [:p] (content summary))

(defsnippet article-list-snip layout-html content-sel
  [model data]
  content-sel (content (map model data)))

(defsnippet pager-snip pager-html [:.pager]
  [link-href curr-page count limit] 
  [:.prev-page] (do-> (if (= curr-page 1)
                        (content "")
                        (set-attr :href (str link-href "/" (- curr-page 1)))))
  [:.next-page] (do-> (if (= curr-page (max count limit))
                        (content "")
                        (set-attr :href (str link-href "/" (+ curr-page 1)))))
  [:.page] (let [pages-limit (min count limit)
                 half-limit (-> pages-limit
                                (/ 2)
                                math/ceil
                                int)
                 left-offset1 (- curr-page half-limit)
                 left-offset2 (+ (- count pages-limit) 1)
                 left-offset (min left-offset1 left-offset2)
                 begin (max 1 left-offset)
                 end (+ begin pages-limit)]
             (clone-for [page (range begin end)]
                        [:a] (do->
                              (content (str page))
                              (if (= curr-page page)
                                (set-attr :href "")
                                (set-attr :href (str link-href "/" page)))
                              (set-attr :class (if (= curr-page page)
                                                 "curr-page"
                                                 "page"))))))

;; templates

(def pages-limit 10)

(deftemplate layout layout-html [menu-items
                                 articles-type
                                 articles-list
                                 curr-page
                                 pages-count]
  leftnav-sel (substitute (leftnav-snip link-snip menu-items))
  content-sel (substitute (article-list-snip article-snip articles-list))
  [:.pager]   (substitute (pager-snip (str "/articles/" articles-type)
                                      curr-page
                                      pages-count
                                      pages-limit)))




