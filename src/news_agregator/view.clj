(ns news_agregator.view
  (:require [net.cgrand.enlive-html :refer :all]))

(def menu-items ["Информационные технологии"
                 "Политика"
                 "Спорт"
                 "Разное"])

// TODO прочитать https://github.com/swannodette/enlive-tutorial

;; pages

(def layout-html (html-resource "public/html/layout.html"))

;; selectors

(def link-sel [[:div#leftnav (nth-of-type 1)] :> first-child])

;; fragments

(def menu-item-frag (select layout-html [:div#leftnav]))

(defn leftnav []
  (at menu-item-frag
      [:p first-child] (clone-for [item menu-items] (content item))))

;; templates

(deftemplate layout "public/html/layout.html" []
  [:div#leftnav] (substitute (leftnav)))

