(ns squid-mvc.seed)

(def data
  [{:type        :todo
    :description "Taste JavaScript"
    :complete    true}
   {:type        :todo
    :description "Buy a unicorn"
    :complete    false}
   {:type        :todo
    :description "Display seed data"
    :complete    true}

   {:db/ident :app
    :new-todo ""
    :page     :all}])
