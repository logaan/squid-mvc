(ns squid-mvc.routes
  ;; (:require
  ;;  [goog.events :as events]
  ;;  [goog.history.EventType :as EventType])
  ;; (:import goog.history.Html5History)
  (:require [accountant.core :as accountant]
            [domkm.silk :as s]
            [bidi.bidi :refer [match-route path-for]]))

(comment
  (defn add-listener [type fn]
    (.addEventListener js/window type fn))

  (aset js/window "onhashchange"
        (fn []
          (println "onhashchange")))

  (aset js/window "onload"
        (fn []
          (println "onload")))

  (add-listener "load"
                (fn [e]
                  (println "onload")))

  (add-listener "popstate"
                (fn [e]
                  (println "popstate")))

  (add-listener "pageshow"
                (fn [e]
                  (println "pageshow"))))

;; - Manually adding a #foo to the url causes onhashchange and popstate to be
;;   triggered.
;; - Pressing back after a #foo change causes both to be triggered.
;; - Calling pushState doesn't cause either to be triggered

;; So I think the two things that turned me off about secretary originally were
;; that it required it's own function to be called to change the path and that
;; it didn't work with links on the page. I think now I understand that calling
;; that function is a way of calling pushState and having the routing trigger as
;; well. And that link handling is perhaps outside the scope of something like
;; this. Plus perhaps it's ok if you have to set a href and an onClick handler
;; for links.

;; Ooh god and also the defroute macro. Also secretary looks like it needs
;; google closure to handle history events. Which I suppose is back/forward in
;; the browser.

;; Quick and dirty history configuration.
;; (let [h (Html5History.)]
;;   (goog.events/listen h EventType/NAVIGATE
;;                       #(js/console.log "NAVIGATE" (.-token %)))
;;   (doto h (.setEnabled true)))

;; Google closure NAVIGATE only seems to run for onload and hashchange. Not
;; popstate.

;; So the purpose of secretary is to allow you to express routes that are
;; bidirectional like silk or bidi. But also to store those routes in a global
;; mutable thing that gets updated with each defroute. And then also call the
;; appropriate controller at the appropriate time. It seems to me that bidi
;; might be a less macrolicious solution. When used in conjunction with
;; accountant who's job is to trigger routing at the appropriate times.

(comment

  (defonce nav-config
    (accountant/configure-navigation!
     {:nav-handler  (fn [path] (js/console.log "nav handler" path))
      :path-exists? (fn [path] (js/console.log "path exists" path))}))

  (accountant/dispatch-current!)

  )

;; nav-handler does not trigger on initial page load
;; can be manually triggered with dispatch-current
;; configure-navigation! just adds more callback each time you call it have to
;; stick it in a fn or a defonce
;; #routes do trigger the navigation but the path is just set to /
;; direct calls to pushState don't trigger anything
;; accountant/navigate! does trigger routes and sets path


;; What I want:
;; - Triggers routes on:
;;   - Initial page load
;;     - window.onload
;;   - Change of # path
;;     - popState
;;   - User pressed back/forward
;;     - popState
;;   - Navigation via pushState
;;     - Doesn't seem like this exists. Have to use your own navigate fn that
;;       wraps pushState.
;; - Bidirectional routing
;;   - Ideally without macros
;; - No circular dependencies with actions
;; - Can take the app as an argument to the actions?
;; - No verbose map of {:user-listing user-listing, etc}


;; You can do equality checking on fns in cljs
;; Works for protocol fns as well

(comment

  (defprotocol Routes
    (all [app])
    (active [app])
    (completed [app]))

  (def routes
    (s/routes
     {all       [[]]
      active    [["active"]]
      completed [["completed"]]}))

  (defrecord Kitten [name]
    Routes
    (all [_]
      (js/console.log "all" name))
    (active [_]
      (js/console.log
       "active action path is:"
       (s/depart routes active)))
    (completed [_]
      (js/console.log "completed" name)))

  (let [app    (Kitten. "fluffy")
        action (s/arrive routes "/active" :domkm.silk/name)]
    (action app))

  )

;; So one nice thing about silk that shouldn't be overlooked is that it'll pull
;; out the query and url params for you. Bidi will basically do the same but the
;; route specification syntax will mean you've got to do a bit more nesting and
;; include a few more slashes. A nicer thing is that you can use :handler rather
;; than :domkm.silk/name but it's not super important.

;; So this method seems ok. It gives the action access to the app. But we do end
;; up listing the routes three times. In the protocol, in the routes and in the
;; actions. Perhaps secretary is preferable.

;; The reason for using the protocol is so that we don't need the action map.
;; But we're not actually saving any lines of code. And we don't need it for the
;; polymorphism. Perhaps it's time to embrace a global app object. We do need to
;; solve the problem on routes and actions referring to each other.

;; I don't think that circular dependency can actually be solved any more nicely
;; than with protocols. If you use keywords then you still need to map the
;; keywords back to fns. If you just use fns directly (like with secretary) then
;; you can't refer to actions that haven't been created yet.

;; ------------------------------ Path handling ------------------------------

(defn prefix [prefix routes]
  (mapcat (fn [[name pattern action]]
            [name (vec (concat prefix pattern)) action])
          (partition 3 routes)))

(defn squid-routes [conn]
  (prefix ["/"]

          [:all [""]
           (fn [{:keys [routes] :as match}]
             (js/console.log match)
             (js/console.log (path-for routes :product :id 1))
             (js/console.log "this is the action for :all"))

           :active ["active"]
           (fn [_]
             (js/console.log "this is the action for :active"))

           :completed ["completed"]
           (fn [_]
             (js/console.log "this is the action for :completed"))

           :product ["products/" :id]
           (fn [{:keys [id routes]}]
             (js/console.log "product route"))]))

(defn add-group-to-routes [out [name pattern action]]
  (-> out
      (update-in [:patterns] assoc pattern name)
      (update-in [:actions] assoc name action)))

(defn transform-routes [routes]
  (let [grouped              (partition 3 routes)
        base                 {:patterns {} :actions {}}
        patterns-and-actions (reduce add-group-to-routes base grouped)]
    (update-in patterns-and-actions [:patterns] (fn [ps] ["" ps]))))

(defn handle-path [routes path]
  (js/console.log "handle-path")
  (let [{:keys [patterns actions]} (transform-routes routes)
        match                      (match-route patterns path)]
    (if-let [action (actions (:handler match))]
      (action (assoc match :routes patterns)))))

;; ------------------------------ Event triggering ------------------------------

;; Calling handle-current as part of register-routes rather than bothering to
;; wait for window.onload
(defn register-routes [routes]
  (let [handle-current #(handle-path routes window.location.pathname)]
    (.addEventListener js/window "popstate" handle-current)
    (handle-current)))

(defonce registered-routes
  (register-routes (squid-routes nil)))

;; - First event should happen as soon as the routes are registered
;; - Event on dispatch called
;; - Event on history (back/forward)
;; - Event on hash history (back/forward)
