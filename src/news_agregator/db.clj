(ns news_agregator.db
  (:require [korma.db :refer :all]))

(def default-conn {:classname "com.postgresql.jdbc.Driver"
                   :subprotocol "postgresql"
                   :user "user"
                   :password "yes"
                   :subname "//127.0.0.1:5740/news_agregator"
                   :delimiters "`"})

(def db-config (postgres {:db "news_agregator"
                     :user "user"
                     :password "yes"}))

(defdb db db-config)