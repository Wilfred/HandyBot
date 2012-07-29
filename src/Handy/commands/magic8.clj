(ns Handy.commands.magic8)

(defn magic8 [{}]
  "Randomly choose a yes-no-maybe response."
  (rand-nth ["Yes." "No." "Definitely." "Of course not!" "Highly likely."
              "Ask yourself, do you really want to know?"
              "I'm telling you, you don't want to know." "Mu!"]))
