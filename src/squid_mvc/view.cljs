(ns squid-mvc.view
  (:require [squid.core :as s]))

(defn render [app db]
  (s/div {}
   (s/header {:class "header"}
             (s/h1 {} "todos")
             (s/input {:class "new-todo"
                       :placeholder "What needs to be done?"
                       :autofocus true}))

   ;; Hide by default. Show when there are todos.
   (s/section {:class "main"}

              (s/input {:class "toggle-all" :type "checkbox"})
              (s/label {:for "toggle-all"} "Mark all as complete")

              (s/ul {:class "todo-list"}
                    (s/li {:class "completed"}
                          (s/div {:class "view"}
                                 (s/input {:class "toggle"
                                           :type "checkbox"
                                           :checked true})
                                 (s/label {} "Taste JavaScript")
                                 (s/button {:class "destroy"}))
                          (s/input {:class "edit"
                                    :value "Create a TodoMVC template"}))

                    (s/li {}
                          (s/div {:class "view"}
                                 (s/input {:class "toggle"
                                           :type "checkbox"})
                                 (s/label {} "Buy a unicorn")
                                 (s/button {:class "destroy"}))
                          (s/input {:class "edit"
                                    :value "Rule the web"}))))

   ;; Hide by default show when there are todos
   (s/footer {:class "footer"}
             ; This should be 0 item left by default
             (s/span {:class "todo-count"}
                     (s/strong {} "0")
                     " item left")

             (s/ul {:class "filters"}
                   (s/li {}
                         (s/a {:class "selected"
                               :href "#/"}
                              "All"))
                   (s/li {}
                         (s/a {:href "#/active"}
                              "Active"))
                   (s/li {}
                         (s/a {:href "#/completed"}
                              "Completed")))

             ;; Hidden if no completed items are left
             (s/button {:class "clear-completed"}
                       "Clear completed"))))

(comment

			<header class="header">
				<h1>todos</h1>
				<input class="new-todo" placeholder="What needs to be done?" autofocus>
			</header>
			<!-- This section should be hidden by default and shown when there are todos -->
			<section class="main">
				<input class="toggle-all" type="checkbox">
				<label for="toggle-all">Mark all as complete</label>
				<ul class="todo-list">
					<!-- These are here just to show the structure of the list items -->
					<!-- List items should get the class `editing` when editing and `completed` when marked as completed -->
					<li class="completed">
						<div class="view">
							<input class="toggle" type="checkbox" checked>
							<label>Taste JavaScript</label>
							<button class="destroy"></button>
						</div>
						<input class="edit" value="Create a TodoMVC template">
					</li>
					<li>
						<div class="view">
							<input class="toggle" type="checkbox">
							<label>Buy a unicorn</label>
							<button class="destroy"></button>
						</div>
						<input class="edit" value="Rule the web">
					</li>
				</ul>
			</section>
			<!-- This footer should hidden by default and shown when there are todos -->
			<footer class="footer">
				<!-- This should be `0 items left` by default -->
				<span class="todo-count"><strong>0</strong> item left</span>
				<!-- Remove this if you don't implement routing -->
				<ul class="filters">
					<li>
						<a class="selected" href="#/">All</a>
					</li>
					<li>
						<a href="#/active">Active</a>
					</li>
					<li>
						<a href="#/completed">Completed</a>
					</li>
				</ul>
				<!-- Hidden if no completed items are left ↓ -->
				<button class="clear-completed">Clear completed</button>
			</footer>

      )
