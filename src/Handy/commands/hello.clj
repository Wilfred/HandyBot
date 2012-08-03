(ns Handy.commands.hello)

(defn hello [{nick :nick}]
  "Greet the user who spoke."
  (format "Hello %s!" nick))