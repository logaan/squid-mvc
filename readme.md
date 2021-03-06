# Squid • [TodoMVC](http://todomvc.com)

This is an implementation of TodoMVC using ClojureScript and the Squid
framework.

Squid follows a MVC structure. Models and views are purely functional code.
Views are memoised and will only re-render when called with new data.
Controllers deal with javascript events, state mutations, ajax calls, etc.
Application state is stored in an in memory database. User interactions trigger
functions that write directly to the database.

Check out the [live demo](https://logaan.github.io/squid-mvc/). Have a look at
the console to see how memoisation dramatically reduces rendering.

## Dependencies

Known to work with

* Java 1.8.0_25
* npm 2.10.1

Should work with any fairly recent version of either.

## Getting started

    ./scripts/npm-install
    ./scripts/figwheel

`npm-install` will pull down the static assets used for styling the TodoMVC
template.

`figwheel` will install Leiningen (the defacto standard ClojureScript build
tool) which will in turn install all remaining Clojurescript dependencies.

## Development

    ./scripts/figwheel

This will start a repl and open a browser window pointing to the application.
Code written in the repl will run in the browser window.

## Credit

Created by [Logan Campbell](https://twitter.com/logaan)

## Functionality requirements

### No todos

- [x] When there are no todos, `#main` and `#footer` should be hidden.

### New todo

- [x] New todos are entered in the input at the top of the app. The input
      element should be focused when the page is loaded, preferably by using the
      `autofocus` input attribute. Pressing Enter creates the todo, appends it
      to the todo list, and clears the input.
- [x] Make sure to `.trim()` the input and then check that it's not empty before
      creating a new todo.

### Mark all as complete

- [x] This checkbox toggles all the todos to the same state as itself.
- [x] Make sure to clear the checked state after the "Clear completed" button
      is clicked.
- [x] The "Mark all as complete" checkbox should also be updated when single
      todo items are checked/unchecked. Eg. When all the todos are checked it
      should also get checked.

### Item

A todo item has three possible interactions:

- [x] Clicking the checkbox marks the todo as complete by updating its
      `completed` value and toggling the class `completed` on its parent `<li>`
- [x] Double-clicking the `<label>` activates editing mode, by toggling the
      `.editing` class on its `<li>`
- [x] Hovering over the todo shows the remove button (`.destroy`)

### Editing

- [x] When editing mode is activated it will hide the other controls and bring
      forward an input that contains the todo title, which should be focused
      (`.focus()`).
- [x] The edit should be saved on both blur and enter, and the `editing` class
      should be removed.
- [x] Make sure to `.trim()` the input and then check that it's not empty. If
      it's empty the todo should instead be destroyed.
- [x] If escape is pressed during the edit, the edit state should be left and
      any changes be discarded.

### Counter

- [x] Displays the number of active todos in a pluralized form.
- [x] Make sure the number is wrapped by a `<strong>` tag.
- [x] Also make sure to pluralize the `item` word correctly: `0 items`, `1
      item`, `2 items`. Example: **2** items left

### Clear completed button

- [x] Removes completed todos when clicked. Should be hidden when there are no
      completed todos.

### Persistence

- [x] Your app should dynamically persist the todos to localStorage. If the
      framework has capabilities for persisting data (e.g. Backbone.sync), use
      that. Otherwise, use vanilla localStorage.
- [x] If possible, use the keys `id`, `title`, `completed` for each item.
- [x] Make sure to use this format for the localStorage name:
      `todos-[framework]`.
- [x] Editing mode should not be persisted.
	1. It’s not explicit here but the backbone implementation discards unsaved
       edit changes.

### Routing

- [x] Routing is required for all implementations. If supported by the
      framework, use its built-in capabilities. Otherwise, use
      the [Flatiron Director](https://github.com/flatiron/director) routing
      library located in the `/assets` folder. The following routes should be
      implemented: `#/` (all - default), `#/active` and `#/completed` (`#!/` is
      also allowed).
- [x] When the route changes, the todo list should be filtered on a model level
      and the `selected` class on the filter links should be toggled.
- [x] When an item is updated while in a filtered state, it should be updated
      accordingly. E.g. if the filter is `Active` and the item is checked, it
      should be hidden.
- [x] Make sure the active filter is persisted on reload.
