(ns squid-mvc.view
  (:require [squid.core :as s]
            [squid-mvc.model :as m]))

(defprotocol Todos
  (edit-new [app])
  (create [app]))

(defn pluralise [word number]
  (str word (if (not= 1 number) "s")))

;; Only re-render if the text of the new-todo changes. Creations of todos will
;; reset the new-todo but deletions/edits/completions should have no impact.
(defn header [app new-todo]
  (s/header {:class "header"}
            (s/h1 "todos")
            (s/form {:onsubmit (create app)}
                    (s/input {:class       "new-todo"
                              :placeholder "What needs to be done?"
                              :autofocus   true
                              :oninput     (edit-new app)
                              :value       new-todo}))))

;; Should not re-render if we write in the new-todo box. But will re-render for
;; any changes to todos as we're presenting all of their information.
(defn main [todos]
  (s/section {:class "main"}

             (s/input {:class "toggle-all"
                       :type  "checkbox"})
             (s/label {:for "toggle-all"}
                      "Mark all as complete")

             (s/ul {:class "todo-list"}
                   (for [{:keys [db/id complete description]} todos]
                     (s/li {:class (if complete "completed")}
                           (s/div {:class "view"}
                                  (s/input {:class   "toggle"
                                            :type    "checkbox"
                                            (if complete :checked) true})
                                  (s/label {} description)
                                  (s/button {:class "destroy"}))
                           (s/input {:class "edit"
                                     :value description}))))))

;; Footer should not re-render if we edit the description of
;; a todo. Only if we create/delete/complete a todo.
(defn footer [incomplete-count show-clear?]
  (s/footer {:class "footer"}
            (s/span {:class "todo-count"}
                    (s/strong {} incomplete-count) " "
                    (pluralise "item" incomplete-count)
                    " left")

            (s/ul {:class "filters"}
                  (s/li {}
                        (s/a {:class "selected"
                              :href  "#/"}
                             "All"))
                  (s/li {}
                        (s/a {:href "#/active"}
                             "Active"))
                  (s/li {}
                        (s/a {:href "#/completed"}
                             "Completed")))

            (if show-clear?
              (s/button {:class "clear-completed"}
                        "Clear completed"))))

(defn render [app db]
  (let [todos (m/todos db)
        {:keys [new-todo] :as ad} (m/app-data db)]
    (s/div {}
           (header app new-todo)
           (if (seq todos)
             (s/div {}
                    (main todos)
                    (footer (m/incomplete-count db)
                            (m/any-complete? db)))))))
