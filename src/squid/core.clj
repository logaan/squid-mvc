(ns squid.core)

(defmacro defn-memo [name & body]
  `(def ~name (memoize (fn ~body))))
