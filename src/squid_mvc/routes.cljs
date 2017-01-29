(ns squid-mvc.routes
  (:require [datascript.core :as d]))

(defn set-page [conn page]
  (d/transact! conn [{:db/ident :app :page page}]))

(defn mvc-routes [conn]
  [:default [""]
   #(set-page conn :all)

   :all ["/"]
   #(set-page conn :all)

   :active ["/active"]
   #(set-page conn :active)

   :completed ["/completed"]
   #(set-page conn :completed)])
