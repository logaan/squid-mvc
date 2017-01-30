(ns squid.routes
  (:require [bidi.bidi :refer [match-route path-for]]
            [clojure.string :as str]))

; --- Helpers ---

(defn prefix [prefix routes]
  (mapcat (fn [[name pattern action]]
            [name (vec (concat prefix pattern)) action])
          (partition 3 routes)))

; --- Routing transformation ---

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
  (let [{:keys [patterns actions]} (transform-routes routes)
        match                      (match-route patterns path)]
    (if-let [action (actions (:handler match))]
      (action (assoc match :routes patterns)))))

;; --- Event triggering ---

(def route-type
  {:hash    (fn [] (str/replace window.location.hash #"^#" ""))
   :history (fn [] window.location.pathname)})

(defn handle-current [type routes]
  (let [path ((route-type type))]
    (js/console.log path)
    (handle-path routes path)))

(defn register-routes [type routes]
  (.addEventListener js/window "popstate" #(handle-current type routes))
  (handle-current type routes))

;; Only for use with :history routing
(defn navigate! [routes name & arguments]
  (let [{:keys [patterns actions]} (transform-routes routes)
        path                       (path-for patterns name arguments)]
    (js/console.log "path: " path)
    (.pushState js/window.history nil, nil, path)
    (handle-current :history routes)))
