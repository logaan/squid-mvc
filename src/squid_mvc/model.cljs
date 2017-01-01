(ns squid-mvc.model
  (:require [datascript.core :as d]))

(defn todos [db]
  (map first
       (d/q '[:find (pull ?e [*])
              :where [?e :type :todo]]
            db)))

(defn- count-by-complete [db complete?]
  (first
   (d/q '[:find [(count ?e)]
          :in $ ?complete?
          :where [?e :complete ?complete?]]
        db complete?)))

(defn any-complete? [db]
  (pos? (count-by-complete db true)))

(defn incomplete-count [db]
  (count-by-complete db false))

(defn app-data [db]
  (d/entity db '[:db/ident :app]))
