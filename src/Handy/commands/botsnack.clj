(ns Handy.commands.botsnack)

(defn botsnack
  [parsed-irc-message]
  (first
   (shuffle
    ["Mmm, thanks!"
     "Oh, tasty!"
     "Yum!"])))
