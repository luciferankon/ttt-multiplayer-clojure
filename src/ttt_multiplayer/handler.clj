(ns ttt-multiplayer.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [ttt-multiplayer.core :as game]
            [ring.middleware.json :refer [wrap-json-body]]))

(def game-state (atom {:current {:moves #{} :symbol :x} :opponent {:moves #{} :symbol :o}}))

(defn get-game [] (json/write-str @game-state))

(defn make-move
  [request]
  (let [position (read-string ((:body request) "position"))]
    (do (swap! game-state (partial game/place-move position))
        (swap! game-state game/change-round)
        (get-game))))

(defroutes app-routes
           (GET "/" [] (get-game))
           (POST "/makemove" req (make-move req)))

(defn -main []
  (jetty/run-jetty (wrap-json-body app-routes) {:port 3000}))
