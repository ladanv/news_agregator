(ns news_agregator.model
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

; articles

(defentity articles)

(defn get-articles
  ([] 
     (select articles))
  ([article-type]
       (select articles
               (where {:type article-type})))
  ([limit-num offset-num]
     (select articles
             (limit limit-num)
             (offset offset-num)))
  ([article-type limit-num offset-num]
     (select articles
             (where {:type article-type})
             (limit limit-num)
             (offset offset-num))))

(first (get-articles "all"))

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





