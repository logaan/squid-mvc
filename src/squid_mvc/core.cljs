(ns squid-mvc.core
  (:require [datascript.core :as d]
            [squid.core :as s]
            [squid-mvc.view :as v]
            [squid-mvc.seed :as seed]
            [squid-mvc.controller :as c]
            [squid-mvc.routes :as r]
            [squid.routes :as sr]
            [alandipert.storage-atom :refer [local-storage] :as sa]))

(enable-console-print!)

(def schema
  {:db/ident {:db/unique :db.unique/identity}
   :editing  {:db/unique :db.unique/identity}})

(defn loaded-from-storage? [load-result]
  (not= (d/empty-db schema) @load-result))

(defonce app
  (let [conn        (d/create-conn schema)
        container   (js/document.getElementById "app")]
    (local-storage conn "todos-squid")
    (if (loaded-from-storage? conn)
      (c/discard-edit conn)
      (d/transact! conn seed/data))
    (sr/register-routes :hash (r/mvc-routes conn))
    (s/mount container conn #'v/render)))

(defn on-js-reload []
  (s/render app))
