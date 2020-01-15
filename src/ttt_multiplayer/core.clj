(ns ttt-multiplayer.core)

(def winning-combinations #{[1 2 3] [4 5 6] [7 8 9] [1 4 7] [2 5 8] [3 6 9] [1 5 9] [3 5 7]})

(defn won?
  [game]
  (let [moves (get-in game [:current :moves])]
    (some (partial every? moves) winning-combinations)))

(defn change-round
  [game]
  (-> game
      (assoc :opponent (:current game))
      (assoc :current (:opponent game))))

(defn count-moves
  [game]
  (apply + (map #(count (get-in game [% :moves])) [:current :opponent])))

(defn draw?
  [game]
  (= (count-moves game) 9))

(defn get-state
  [state result]
  {:status state :result result})

(defn over?
  [game]
  (cond
    (won? game) (get-state :over (get-in game [:current :symbol]))
    (draw? game) (get-state :over :draw)
    :always (get-state :inplay nil)))

(defn place-move
  [move game]
  (let [game-with-move (->> move
                            (update (:current game) :moves conj)
                            (assoc game :current))]
    (merge game-with-move (over? game-with-move))))

(defn make-pairs
  [moves symbol]
  (mapcat vector (map dec moves) (repeat symbol)))


(defn get-board
  [game]
  (let [board (into [] (repeat 9 " "))]
    (apply assoc board (concat (mapcat
                                 #(make-pairs (get-in game [% :moves]) (get-in game [% :symbol]))
                                 [:opponent :current])))))

(defn print-board
  [game]
  (->> game
       (get-board)
       (partition 3)
       (map (partial apply str))
       (clojure.string/join "\n-------\n")
       (println)))

(defn game-over?
  [game]
  (= :over (:status game)))
