(ns squid-mvc.seed)

(def data
  [{:type        :todo
    :description "Taste JavaScript"
    :complete    true}
   {:type        :todo
    :description "Buy a unicorn"
    :complete    false}
   {:db/ident :app
    :new-task ""}])
