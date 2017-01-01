# Squid • [TodoMVC](http://todomvc.com)

This is an implementation of TodoMVC using ClojureScript and the Squid
framework. Squid follows a MVC structure. Models and views are purely functional
code. Only the controller deals with javascript events, state mutations, ajax
calls, etc. Your application's state is stored in a in memory database. User
interactions trigger functions that write directly to the database.

Check out the [live demo](https://logaan.github.io/squid-mvc/).

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
