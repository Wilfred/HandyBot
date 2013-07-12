(ns Handy.parsing)

(defn parse-irc-message
  "Separate RAW-MESSAGE into a map of the different parts: the
message, the user, etc."
  [raw-message]
  (let [[whole-message nick user host message-type channel message]
        (re-find #":(.*?)!(.*?)@(.*?) (.*?) (.*?) :(.*)" raw-message)
        [_ command-name _ argument]
        (re-find #"^([^ ]+)( (.*))?" message)]
    {:nick nick :user user :host host :message-type message-type
     :channel channel :message message
     :command-name command-name :argument argument}))
