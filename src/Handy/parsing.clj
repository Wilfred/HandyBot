(ns Handy.parsing
  (:use [clojure.string :only [trim]]))

;; todo: factor out the bot command prefix

(defn parse-irc-message
  "Separate RAW-MESSAGE into a map of the different parts: the
message, the user, etc."
  [raw-message]
  (let [[whole-message nick user host message-type channel message]
        (re-find #":(.*?)!(.*?)@(.*?) (.*?) (.*?) :(.*)" raw-message)]
    {:nick nick :user user :host host :message-type message-type
     :channel channel :message message}))

(defn parse-bot-message
  "Separate RAW-MESSAGE into the command name, its argument, and IRC
information. Returns nil if this raw messsage doesn't look like a bot
command."
  [raw-message]
  (let [parsed-irc-message (parse-irc-message raw-message)]
    (when-let [match (re-find #"^(%[^ ]+)|HandyBot:(.*)" (parsed-irc-message :message))]
      (let [[_ command-name argument] match]
        (conj parsed-irc-message
              {:command-name command-name :argument (trim argument)})))))

