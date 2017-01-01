(ns squid-mvc.controller
  (:require [squid.core :as s]
            [datascript.core :as d]
            [squid-mvc.model :as m]
            [squid-mvc.view :refer [Todos]]))

(extend-type s/App
  Todos
  (edit-new [{:keys [state]}]
    (fn [event]
      (d/transact! state
                   [{:db/ident :app
                     :new-todo event.target.value}])))

  (create [{:keys [state]}]
    (fn [event]
      (.preventDefault event)
      (let [{:keys [new-todo]} (m/app-data @state)]
        (d/transact! state
                     [{:type        :todo
                       :description new-todo
                       :complete    false}
                      {:db/ident :app
                       :new-todo ""}]))))

  (edit
    ([{:keys [state]} id attr]
     (fn [event]
       (d/transact! state [[:db/add id attr event.target.value]])))

    ([{:keys [state]} id attr value]
     (fn [_]
       (d/transact! state [[:db/add id attr value]]))))

  (toggle-complete [{:keys [state]} id]
    (fn [_]
      (let [{:keys [complete]} (d/entity @state id)]
        (d/transact! state [[:db/add id :complete (not complete)]]))))

  (destroy [{:keys [state]} id]
    (fn [_]
      (d/transact! state [[:db.fn/retractEntity id]])))

  (clear-completed [{:keys [state]}]
    (fn [_]
      (let [ids         (map :db/id (m/find-by-complete @state true))
            retractions (for [id ids] [:db.fn/retractEntity id])]
        (d/transact! state retractions)))))
