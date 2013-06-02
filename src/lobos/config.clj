(ns lobos.config
  ;; (:use lobos.connectivity
  ;;       news_agregator.db))
  (:require [lobos.connectivity :refer :all]
            [news_agregator.db :refer :all]))

(open-global db-config)