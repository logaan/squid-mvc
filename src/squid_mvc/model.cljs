(ns squid-mvc.model
  (:require [datascript.core :as d]))

(def page->complete
  {:active    false
   :completed true})

(defn- all-todos [db]
  (map first
       (d/q '[:find (pull ?e [*])
              :where [?e :type :todo]]
            db)))

(defn any-todos? [db]
  (pos? (count (all-todos db))))

(defn find-by-complete [db complete?]
  (map first
       (d/q '[:find (pull ?e [*])
              :in $ ?complete?
              :where [?e :complete ?complete?]]
            db complete?)))

(defn todos [db page]
  (sort-by :db/id
           (if (= page :all)
             (all-todos db)
             (find-by-complete db (page->complete page)))))

(defn- count-by-complete [db complete?]
  (count (find-by-complete db complete?)))

(defn any-complete? [db]
  (pos? (count-by-complete db true)))

(defn incomplete-count [db]
  (count-by-complete db false))

(defn any-incomplete? [db]
  (pos? (incomplete-count db)))

(defn all-complete? [db]
  (zero? (incomplete-count db)))

(defn app-data [db]
  (d/entity db [:db/ident :app]))
