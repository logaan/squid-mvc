(ns squid.core
  (:require [cljsjs.virtual-dom]))

(defprotocol Render
  (render [app]))

(defrecord App [vdom state renderer]
  Render
  (render [app]
    (let [{:keys [tree root]} @vdom
          new-tree            (renderer state)
          patches             (js/virtualDom.diff tree new-tree)
          new-root            (js/virtualDom.patch root patches)]
      (swap! vdom assoc :tree new-tree :root new-root))))

(defn mount [mount-point state renderer]
  (let [vdom (atom {:tree nil :root nil})
        app  (App. vdom state renderer)
        tree (renderer state)
        root (js/virtualDom.create tree)]
    (swap! vdom assoc :tree tree :root root)
    (.appendChild mount-point root)
    (add-watch state ::re-render (fn [_ _ _ _] (render app)))
    app))

(def pass-through-attrs
  [:value :onsubmit :oninput :ondblclick :onblur :onclick])

(defn h [tag attrs & children]
  (let [options (merge {:attributes attrs}
                       (select-keys attrs pass-through-attrs))]
    (js/virtualDom.h tag
                    (clj->js options)
                    (clj->js children))))

;; Only defining the elements used by TodoMVC
(def header (partial h "header"))
(def h1 (partial h "h1"))
(def input (partial h "input"))
(def section (partial h "section"))
(def label (partial h "label"))
(def ul (partial h "ul"))
(def li (partial h "li"))
(def div (partial h "div"))
(def button (partial h "button"))
(def footer (partial h "footer"))
(def span (partial h "span"))
(def strong (partial h "strong"))
(def a (partial h "a"))

(def form (partial h "form"))
