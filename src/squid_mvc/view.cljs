(ns squid-mvc.view
  (:require-macros [squid.core :as s])
  (:require [squid.core :as s]
            [squid-mvc.model :as m]))

(defprotocol Todos
  (edit-new [conn])
  (create [conn])
  (edit [conn id attr] [conn id attr value])
  (toggle-complete [conn id])
  (destroy [conn id])
  (clear-completed [conn]))

(defn pluralise [word number]
  (str word (if (not= 1 number) "s")))

(s/defn-memo header [conn new-todo]
  (println "header")
  (s/header {:class "header"}
            (s/h1 {} "todos")
            (s/form {:onsubmit (create conn)}
                    (s/input {:class       "new-todo"
                              :placeholder "What needs to be done?"
                              :autofocus   true
                              :oninput     (edit-new conn)
                              :value       new-todo}))))

(s/defn-memo main [conn todos]
  (println "main")
  (s/section {:class "main"}

             (s/input {:class "toggle-all"
                       :type  "checkbox"})
             (s/label {:for "toggle-all"}
                      "Mark all as complete")

             (s/ul {:class "todo-list"}
                   (for [{:keys [db/id complete description editing]} todos]
                     (s/li {:class      (str (if complete "completed") " "
                                             (if editing "editing"))
                            :ondblclick (edit conn id :editing true)}
                           (s/div {:class "view"}
                                  (s/input {:class                 "toggle"
                                            :type                  "checkbox"
                                            (if complete :checked) true
                                            :onclick               (toggle-complete conn id)})
                                  (s/label {} description)
                                  (s/button {:class   "destroy"
                                             :onclick (destroy conn id)}))
                           (s/input {:class   "edit"
                                     :value   description
                                     :onblur  (edit conn id :editing false)
                                     :oninput (edit conn id :description)}))))))

(s/defn-memo footer [conn incomplete-count show-clear?]
  (println "footer")
  (s/footer {:class "footer"}
            (s/span {:class "todo-count"}
                    (s/strong {} incomplete-count) " "
                    (pluralise "item" incomplete-count)
                    " left")

            (if show-clear?
              (s/button {:class "clear-completed"
                         :onclick (clear-completed conn)}
                        "Clear completed"))))

(defn render [conn]
  (println "----------------------------------- render -------------------------------------")
  (let [db                        @conn
        todos                     (m/todos db)
        {:keys [new-todo] :as ad} (m/app-data db)]
    (s/div {}
           (header conn new-todo)
           (if (seq todos)
             (s/div {}
                    (main conn todos)
                    (footer conn
                            (m/incomplete-count db)
                            (m/any-complete? db)))))))
