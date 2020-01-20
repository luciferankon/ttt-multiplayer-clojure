(ns ttt-multiplayer.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [ttt-multiplayer.core :as game]
            [ring.util.request :as request]))

(defn generate-response
  [body]
  {
   :status  200
   :headers {"Access-Control-Allow-Origin" "*"}
   :body    body
   })

(def game-state (atom {:current {:moves #{} :symbol :x} :opponent {:moves #{} :symbol :o} :board (repeat 9 " ")}))

(defn add-board
  [game]
  (conj game [:board (game/get-board game)]))


(defn update-game
  [position game-state]
  (->> game-state
       (game/place-move position)
       game/change-round
       add-board))

(defn get-position [req]
  (read-string ((json/read-str (request/body-string req)) "position")))

(defn get-game [] (generate-response (json/write-str @game-state)))

(defn make-move
  [request]
  (if (game/game-over? @game-state)
    (generate-response "game has already finished")
    (do (swap! game-state (partial update-game (get-position request)))
        (get-game))))

(defroutes app-routes
           (GET "/" [] (get-game))
           (POST "/makemove" req (make-move req)))

(defn -main []
  (jetty/run-jetty app-routes {:port 3000}))