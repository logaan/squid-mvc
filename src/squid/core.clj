(ns squid.core)

(defmacro defm [name & body]
  `(def ~name (memoize (fn ~body))))
