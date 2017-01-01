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
