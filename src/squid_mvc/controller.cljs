(ns squid-mvc.controller
  (:require [squid.core :as s]
            [datascript.core :as d]
            [squid-mvc.model :as m]
            [squid-mvc.view :refer [Todos]]
            [clojure.string :as str]))

(extend-type Atom
  Todos
  (edit-new [conn]
    (fn [event]
      (d/transact! conn [{:db/ident :app
                          :new-todo event.target.value}])))

  (create [conn]
    (fn [event]
      (.preventDefault event)
      (let [new-todo (str/trim (:new-todo (m/app-data @conn)))]
        (if (empty? new-todo)
          (d/transact! conn [{:db/ident :app
                              :new-todo ""}])
          (d/transact! conn [{:type        :todo
                             :description new-todo
                             :complete    false}
                            {:db/ident :app
                             :new-todo ""}])))))

  (edit
    ([conn id attr]
     (fn [event] (d/transact! conn [[:db/add id attr event.target.value]])))

    ([conn id attr value]
     (fn [_] (d/transact! conn [[:db/add id attr value]]))))

  (toggle-complete [conn id]
    (fn [_]
      (let [{:keys [complete]} (d/entity @conn id)]
        (d/transact! conn [[:db/add id :complete (not complete)]]))))

  (destroy [conn id]
    (fn [_] (d/transact! conn [[:db.fn/retractEntity id]])))

  (clear-completed [conn]
    (fn [_]
      (let [ids         (map :db/id (m/find-by-complete @conn true))
            retractions (for [id ids] [:db.fn/retractEntity id])]
        (d/transact! conn retractions))))

  (toggle-all [conn]
    (fn [_]
      (let [new-compete (m/any-incomplete? @conn)
            ids         (map :db/id (m/todos @conn))
            adds        (for [id ids] [:db/add id :complete new-compete])]
        (d/transact! conn adds)))))
