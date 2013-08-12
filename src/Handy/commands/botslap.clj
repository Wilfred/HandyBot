(ns Handy.commands.botslap)

(defn botslap
  [parsed-irc-message]
  (first
   (shuffle
    ["Ouch!"
     "Ow!"
     "Yowch!"
     "How about sending a pull request rather than violence?"])))
