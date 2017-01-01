(ns squid-mvc.model
  (:require [datascript.core :as d]))

(defn todos [db]
  (map first
       (d/q '[:find (pull ?e [*])
              :where [?e :type :todo]]
            db)))

(defn find-by-complete [db complete?]
  (map first
       (d/q '[:find (pull ?e [*])
          :in $ ?complete?
          :where [?e :complete ?complete?]]
        db complete?)))

(defn- count-by-complete [db complete?]
  (let [[raw-count] (d/q '[:find [(count ?e)]
                           :in $ ?complete?
                           :where [?e :complete ?complete?]]
                         db complete?)]
    (or raw-count 0)))

(defn any-complete? [db]
  (pos? (count-by-complete db true)))

(defn incomplete-count [db]
  (count-by-complete db false))

(defn app-data [db]
  (d/entity db [:db/ident :app]))
