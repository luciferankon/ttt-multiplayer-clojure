(ns ttt-multiplayer.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(defn -main []
  (jetty/run-jetty app-routes {:port 3000}))
