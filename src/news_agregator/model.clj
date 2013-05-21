(ns parse-enlive-html.model
  (:use [korma db core]))

(def default-conn {:classname "com.postgresql.jdbc.Driver"
                   :subprotocol "postgresql"
                   :user "user"
                   :password "yes"
                   :subname "//127.0.0.1:5740/news_agregator"
                   :delimiters "`"})

(defdb db (postgres {:db "news_agregator"
                     :user "user"
                     :password "yes"}))

; article_types

(defentity article_types)

; articles

(defentity articles
  (belongs-to article_types))

(defn find-article-by-hashcode [hashcode]
  (select articles
          (where {:hashcode hashcode})))

(defn article-exists? [article]
  (let [hashcode (:hashcode article)]
    (if (= (find-article-by-hashcode hashcode) [])
      false
      true)))

(defn add-article [article]
 (insert articles (values article)))