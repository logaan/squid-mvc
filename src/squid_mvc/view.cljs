(ns squid-mvc.view
  (:require-macros [squid.core :as s])
  (:require [squid.core :refer [h] :as s]
            [squid-mvc.model :as m]
            [squid-mvc.controller :as c]))

(defn pluralise [word number]
  (str word (if (not= 1 number) "s")))

(defn header [conn new-todo]
  (println "header")
  (h :header.header
     (h :h1 "todos")
     (h :input.new-todo
        {:name        "new-todo"
         :placeholder "What needs to be done?"
         :autofocus   true
         :onkeyup     (c/edit-new conn)
         :value       new-todo})))

(defn main [conn todos all-complete?]
  (println "main")
  (h :section.main

     (h :input.toggle-all
        {:name     "toggle-all"
         :type     "checkbox"
         :checked  all-complete?
         :onchange (c/toggle-all conn)})
     (h :label {:for "toggle-all"}
        "Mark all as complete")

     (h :ul.todo-list
        (for [{:keys [db/id complete description editing]} todos]
          (h :li {:class (str (if complete "completed") " "
                              (if editing "editing"))}
             (h :div.view
                (h :input.toggle
                   {:name    [id "complete"]
                    :type    "checkbox"
                    :checked complete
                    :onclick (c/toggle-complete conn id)})
                (h :label
                   {:name       [id "label"]
                    :ondblclick (c/start-edit conn id)}
                   description)
                (h :button.destroy
                   {:_key    [id "destroy"]
                    :onclick (c/destroy conn id)}))
             (h :input.edit
                {:name    [id "description"]
                 :value   description
                 :onblur  (c/stop-edit conn)
                 :onkeyup (c/perform-edit conn)}))))))

(defn footer [conn page incomplete-count show-clear?]
  (println "footer")
  (h :footer.footer
     (h :span.todo-count
        (h :strong {} incomplete-count) " "
        (pluralise "item" incomplete-count)
        " left")

     (h :ul.filters
        (h :li {}
           (h :a {:href "#/"
                  :class (if (= page :all) "selected")}
              "All"))
        (h :li {}
           (h :a {:href "#/active"
                  :class (if (= page :active) "selected")}
              "Active"))
        (h :li {}
           (h :a {:href "#/completed"
                  :class (if (= page :completed) "selected")}
              "Completed")))

     (if show-clear?
       (h :button.clear-completed
          {:onclick (c/clear-completed conn)}
          "Clear completed"))))

(defn render [conn]
  (println "---------------------------- render ------------------------------")
  (let [db                      @conn
        {:keys [new-todo page]} (m/app-data db)
        todos                   (m/todos db page)]
    (h :div {}
       (header conn new-todo)
       (if (seq todos)
         (main conn todos (m/all-complete? db)))
       (if (m/any-todos? db)
         (footer conn
                 page
                 (m/incomplete-count db)
                 (m/any-complete? db))))))
