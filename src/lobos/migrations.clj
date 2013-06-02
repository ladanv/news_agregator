(ns lobos.migrations
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  ;; (:use (lobos [migration :only [defmigration]] core schema
  ;;              config helpers))
  (:require (lobos [migration :refer [defmigration]]
                   [core :refer :all]
                   [schema :refer :all]
                   [config :refer :all]
                   [helpers :refer :all]))
  )

(defmigration add-articles-table
  (up []
      (create
       (tbl :articles
            (text :headline :not-null)
            ;; (check :headline (> (length :headline) 0))
            (text :link :not-null)
            (text :summary :not-null)
            ;; id by default
            ;; (bigint :id :unique)
            (bigint :hashcode :not-null)
            (text :type :not-null)))

      (create (index :articles [:type]))
      (create (index :articles [:hashcode])))

  (down []
        (drop (table :articles))))

;; (migrate)