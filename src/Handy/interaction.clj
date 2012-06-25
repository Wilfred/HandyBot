(ns Handy.interaction
  "Functions that make the bot speak in a channel.")

(defn say-hello [{nick :nick}]
  "Greet the user who spoke."
  (format "Hello %s!" nick))
