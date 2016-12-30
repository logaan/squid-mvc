(ns squid-mvc.view
  (:require [squid.core :as s]))

(defn header []
  (s/header {:class "header"}
            (s/h1 {} "todos")
            (s/input {:class "new-todo"
                      :placeholder "What needs to be done?"
                      :autofocus true})))

;; Hide by default. Show when there are todos.
(defn main []
  (s/section {:class "main"}

             (s/input {:class "toggle-all"
                       :type  "checkbox"})
             (s/label {:for "toggle-all"}
                      "Mark all as complete")

             (s/ul {:class "todo-list"}
                   (s/li {:class "completed"}
                         (s/div {:class "view"}
                                (s/input {:class   "toggle"
                                          :type    "checkbox"
                                          :checked true})
                                (s/label {} "Taste JavaScript")
                                (s/button {:class "destroy"}))
                         (s/input {:class "edit"
                                   :value "Create a TodoMVC template"}))

                   (s/li {}
                         (s/div {:class "view"}
                                (s/input {:class "toggle"
                                          :type  "checkbox"})
                                (s/label {} "Buy a unicorn")
                                (s/button {:class "destroy"}))
                         (s/input {:class "edit"
                                   :value "Rule the web"})))))

;; Hide by default show when there are todos
(defn footer []
  (s/footer {:class "footer"}
            ;; This should be 0 item left by default
            (s/span {:class "todo-count"}
                    (s/strong {} "0")
                    " item left")

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

            ;; Hidden if no completed items are left
            (s/button {:class "clear-completed"}
                      "Clear completed")))

(defn render [app db]
  (s/div {}
   (header)
   (main)
   (footer)))
