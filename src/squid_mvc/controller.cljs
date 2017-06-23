(ns squid-mvc.controller
  (:require [squid.core :as s]
            [datascript.core :as d]
            [squid-mvc.model :as m]
            [clojure.string :as str]))

(defn- create [conn]
  (let [new-todo (str/trim (:new-todo (m/app-data @conn)))
        actions  (if (empty? new-todo)
                   [{:db/ident :app
                     :new-todo ""}]
                   [{:type        :todo
                     :description new-todo
                     :complete    false}
                    {:db/ident :app
                     :new-todo ""}])]
    (d/transact! conn actions)))

(defn- commit-edit [conn]
  (let [id [:editing true]]
    (if-let [entity (d/entity @conn id)]
      (let [final-desc (-> entity :description str/trim)
            action     (if (empty? final-desc)
                         [:db.fn/retractEntity id]
                         [:db/retract id :editing true])]
        (d/transact! conn [action])))))

(defn- discard-edit [conn]
  (let [id [:editing true]]
    (if-let [{:keys [original-description]} (d/entity @conn id)]
      (d/transact! conn [[:db/add id :description original-description]
                         [:db/retract id :editing true]]))))

(defn edit-new [conn]
  (fn [event]
    (if (= js/event.code "Enter")
      (create conn)
      (d/transact! conn [{:db/ident :app
                          :new-todo event.target.value}]))))

(defn start-edit [conn id]
  (fn [event]
    (let [{:keys [description]} (d/entity @conn id)]
      (d/transact! conn [[:db/add id :original-description description]
                         [:db/add id :editing true]])
      (.focus js/event.target.parentElement.nextElementSibling))))

(defn stop-edit [conn]
  (fn [event]
    (commit-edit conn)))

(defn perform-edit [conn]
  (fn [event]
    (condp = js/event.code
      "Enter"  (commit-edit conn)
      "Escape" (discard-edit conn)
      (d/transact! conn [[:db/add [:editing true]
                          :description event.target.value]]))))

(defn toggle-complete [conn id]
  (fn [_]
    (let [{:keys [complete]} (d/entity @conn id)]
      (d/transact! conn [[:db/add id :complete (not complete)]]))))

(defn destroy [conn id]
  (fn [_] (d/transact! conn [[:db.fn/retractEntity id]])))

(defn clear-completed [conn]
  (fn [_]
    (let [ids         (map :db/id (m/find-by-complete @conn true))
          retractions (for [id ids] [:db.fn/retractEntity id])]
      (d/transact! conn retractions))))

(defn toggle-all [conn]
  (fn [_]
    (let [new-compete (m/any-incomplete? @conn)
          ids         (map :db/id (m/todos @conn :all))
          adds        (for [id ids] [:db/add id :complete new-compete])]
      (d/transact! conn adds))))
