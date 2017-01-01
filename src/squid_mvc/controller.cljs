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
                       :new-todo ""}])))))
