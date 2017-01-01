(ns squid-mvc.core
  (:require [datascript.core :as d]
            [squid.core :as s]
            [squid-mvc.view :as v]
            [squid-mvc.seed :as seed]
            squid-mvc.controller))

(enable-console-print!)

(def schema
  {:db/ident {:db/unique :db.unique/identity}})

(defonce app
  (let [conn      (d/create-conn schema)
        container (js/document.getElementById "app")]
    (d/transact! conn seed/data)
    (s/mount container conn #'v/render)))

(defn on-js-reload []
  (s/render app))
