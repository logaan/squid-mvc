(ns squid-mvc.view
  (:require [squid.core :as s]))

(defn render [app db]
  (s/div {} "Hello World"))
