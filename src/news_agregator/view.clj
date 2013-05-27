(ns news_agregator.view
  (:require [net.cgrand.enlive-html :refer :all]))

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

 ;; TODO stoped here
(defsnippet pager-snip pager-html [:.pager]
  [link-href curr-page count limit] 
  ;; [:.prev-page] (do->
  ;;                (if (= curr-page 1)))
  ;; [:.next-page] ()
  [:.page-link] (clone-for [page (range count)]
                           [:a] (do->
                                 (content (str page))
                                 (set-attr :href (str link-href "/" page)))))

;; (defsnippet my-snip pager-html [:.pager]
;;   [data]
;;   [:.pages :a] (clone-for [item (range 3 13)]
;;                           [:a] (do->
;;                                 (content (str item))
;;                                 (set-attr :href (str item)))))
;; (print (my-snip my-data))

;; templates

(deftemplate layout layout-html [articles-type menu-items article-list]
  leftnav-sel (substitute (leftnav-snip link-snip menu-items))
  content-sel (substitute (article-list-snip article-snip article-list))
  [:.pager]   (substitute (pager-snip (str "/articles" article-type)
                                      (count article-list)
                                      10)))




