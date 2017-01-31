(ns squid-mvc.view
  (:require-macros [squid.core :as s])
  (:require [squid.core :as s]
            [squid-mvc.model :as m]
            [squid-mvc.controller :as c]))

(defn pluralise [word number]
  (str word (if (not= 1 number) "s")))

(s/defn-memo header [conn new-todo]
  (println "header")
  (s/header {:class "header"}
            (s/h1 {} "todos")
            (s/input {:class       "new-todo"
                      :placeholder "What needs to be done?"
                      :autofocus   true
                      :onkeyup     (c/edit-new conn)
                      :value       new-todo})))

(s/defn-memo main [conn todos all-complete?]
  (println "main")
  (s/section {:class "main"}

             (s/input {:class    "toggle-all"
                       :type     "checkbox"
                       :checked  all-complete?
                       :onchange (c/toggle-all conn)})
             (s/label {:for "toggle-all"}
                      "Mark all as complete")

             (s/ul {:class "todo-list"}
                   (for [{:keys [db/id complete description editing]} todos]
                     (s/li {:class (str (if complete "completed") " "
                                        (if editing "editing"))}
                           (s/div {:class "view"}
                                  (s/input {:class   "toggle"
                                            :type    "checkbox"
                                            :checked complete
                                            :onclick (c/toggle-complete conn id)})
                                  (s/label {:ondblclick (c/start-edit conn id)}
                                           description)
                                  (s/button {:class   "destroy"
                                             :onclick (c/destroy conn id)}))
                           (s/input {:class   "edit"
                                     :value   description
                                     :onblur  (c/stop-edit conn)
                                     :onkeyup (c/perform-edit conn)}))))))

(s/defn-memo footer [conn page incomplete-count show-clear?]
  (println "footer")
  (s/footer {:class "footer"}
            (s/span {:class "todo-count"}
                    (s/strong {} incomplete-count) " "
                    (pluralise "item" incomplete-count)
                    " left")

            (s/ul {:class "filters"}
                  (s/li {}
                        (s/a {:href "#/"
                              :class (if (= page :all) "selected")}
                             "All"))
                  (s/li {}
                        (s/a {:href "#/active"
                              :class (if (= page :active) "selected")}
                             "Active"))
                  (s/li {}
                        (s/a {:href "#/completed"
                              :class (if (= page :completed) "selected")}
                             "Completed")))

            (if show-clear?
              (s/button {:class   "clear-completed"
                         :onclick (c/clear-completed conn)}
                        "Clear completed"))))

(defn render [conn]
  (println "----------------------------------- render -------------------------------------")
  (let [db                      @conn
        {:keys [new-todo page]} (m/app-data db)
        todos                   (m/todos db page)]
    (s/div {}
           (header conn new-todo)
           (if (seq todos)
             (main conn todos (m/all-complete? db)))
           (if (m/any-todos? db)
             (footer conn
                     page
                     (m/incomplete-count db)
                     (m/any-complete? db))))))
