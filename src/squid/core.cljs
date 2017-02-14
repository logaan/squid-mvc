(ns squid.core)

(defprotocol Render
  (render [app]))

(defrecord App [vdom state renderer]
  Render
  (render [app]
    (.redraw @vdom)))

(defn mount [mount-point state renderer]
  (let [view (js/domvm.createView (fn [] #(renderer state)))
        vdom (atom view)
        app  (App. vdom state renderer)]
    (.mount view mount-point)
    (add-watch state ::re-render (fn [_ _ _ _] (render app)))
    app))

(defn h [tag & attrs?-&-children]
  (let [[attrs? & children] attrs?-&-children]
    (if (map? attrs?)
      (js/domvm.defineElement (name tag) (clj->js attrs?) (clj->js children))
      (js/domvm.defineElement (name tag) (clj->js attrs?-&-children)))))
