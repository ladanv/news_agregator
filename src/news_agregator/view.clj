(ns news_agregator.view
  (:require [net.cgrand.enlive-html :refer :all]))

;; pages

(def layout-html "public/html/layout.html")

;; selectors

(def leftnav-sel [:#leftnav])
(def link-sel [leftnav-sel :> first-child])
(def content-sel [:#content])
(def article-sel [[:.article (nth-of-type 1)]])

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

;; templates

(deftemplate layout layout-html [menu-items article-list]
  leftnav-sel (substitute (leftnav-snip link-snip menu-items))
  content-sel (substitute (article-list-snip article-snip article-list)))