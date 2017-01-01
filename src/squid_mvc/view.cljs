(ns squid-mvc.view
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

;; Only re-render if the text of the new-todo changes. Creations of todos will
;; reset the new-todo but deletions/edits/completions should have no impact.
(defn header [conn new-todo]
  (s/header {:class "header"}
            (s/h1 {} "todos")
            (s/form {:onsubmit (create conn)}
                    (s/input {:class       "new-todo"
                              :placeholder "What needs to be done?"
                              :autofocus   true
                              :oninput     (edit-new conn)
                              :value       new-todo}))))

;; Should not re-render if we write in the new-todo box. But will re-render for
;; any changes to todos as we're presenting all of their information.
(defn main [conn todos]
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

;; Footer should not re-render if we edit the description of
;; a todo. Only if we create/delete/complete a todo.
(defn footer [conn incomplete-count show-clear?]
  (s/footer {:class "footer"}
            (s/span {:class "todo-count"}
                    (s/strong {} incomplete-count) " "
                    (pluralise "item" incomplete-count)
                    " left")

            ;; Commented out until routing is implemented
            #_(s/ul {:class "filters"}
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
              (s/button {:class "clear-completed"
                         :onclick (clear-completed conn)}
                        "Clear completed"))))

(defn render [conn]
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
