(ns squid-mvc.model
  (:require [datascript.core :as d]))

(defn todos [db]
  (sort-by :db/id
           (map first
                (d/q '[:find (pull ?e [*])
                       :where [?e :type :todo]]
                     db))))

(defn find-by-complete [db complete?]
  (map first
       (d/q '[:find (pull ?e [*])
          :in $ ?complete?
          :where [?e :complete ?complete?]]
        db complete?)))

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
