(ns squid-mvc.view
  (:require [squid.core :as s]
            [squid-mvc.model :as m]))

(defn pluralise [word number]
  (str word (if (not= 1 number) "s")))

(defn header []
  (s/header {:class "header"}
            (s/h1 {} "todos")
            (s/input {:class       "new-todo"
                      :placeholder "What needs to be done?"
                      :autofocus   true})))

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
  (let [todos (m/todos db)]
    (s/div {}
           (header)
           (if (seq todos)
             (s/div {}
                    (main todos)
                    (footer (m/incomplete-count db)
                            (m/any-complete? db)))))))
