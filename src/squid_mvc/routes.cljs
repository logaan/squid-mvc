(ns squid-mvc.routes
  (:require [bidi.bidi :refer [match-route path-for]]
            [datascript.core :as d]
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

(defn register-routes [routes]
  (let [handle-current #(let [unhashed (str/replace window.location.hash #"^#" "")]
                          (handle-path routes unhashed))]
    (.addEventListener js/window "popstate" handle-current)
    (handle-current)))

;; --- App specific ---
(defn set-page [conn page]
  (d/transact! conn [{:db/ident :app :page page}]))

(defn mvc-routes [conn]
  [:default [""]
   (fn []
     (set-page conn :all))

   :all ["/"]
   (fn []
     (set-page conn :all))

   :active ["/active"]
   (fn [_]
     (set-page conn :active))

   :completed ["/completed"]
   (fn [_]
     (set-page conn :completed))])
