(ns Handy.interaction
  "Functions that make the bot speak in a channel."
  (:use [clojure.string :only [trim]]))

;; todo: factor out the bot command prefix

;; todo: fix the duplication with connection.clj of send-to-server and join-channel
(defn send-to-server [server-connection raw-message]
  (let [to-server (:to-server server-connection)]
    (.print to-server (format "%s\r\n" raw-message))
    (.flush to-server)))

(defn say-in-channel [server-connection channel message]
  (send-to-server server-connection (format "PRIVMSG %s :%s" channel message)))

(defn parse-irc-message [raw-message]
  "Separate RAW-MESSAGE into a map of the different parts: the
message, the user, etc."
  (let [[whole-message nick user host message-type channel message]
        (re-find #":(.*?)!(.*?)@(.*?) (.*?) (.*?) :(.*)" raw-message)]
    {:nick nick :user user :host host :message-type message-type
     :channel channel :message message}))

(defn parse-bot-message [raw-message]
  "Separate RAW-MESSAGE into the command name, its argument, and IRC
information. Returns nil if this raw messsage doesn't look like a bot
command."
  (let [parsed-irc-message (parse-irc-message raw-message)]
    (when-let [match (re-find #"%([^ ]+)(.*)" (parsed-irc-message :message))]
      (let [[_ command-name argument] match]
        (conj parsed-irc-message
              {:command-name command-name :argument (trim argument)})))))

(defn say-hello [{nick :nick}]
  "Greet the user who spoke."
  (format "Hello %s!" nick))

(defn join-channel [{channel :argument}]
  (format "JOIN %s" channel))

(defn dispatch-command [server-connection raw-message]
  (when-let [parsed-bot-message (parse-bot-message raw-message)]
    (cond
     (= (parsed-bot-message :command-name) "hello")
     (say-in-channel server-connection (parsed-bot-message :channel) (say-hello parsed-bot-message))

     (= (parsed-bot-message :command-name) "join")
     (send-to-server server-connection (join-channel parsed-bot-message))

     true
     (say-in-channel server-connection (parsed-bot-message :channel) "Sorry, I don't know how to do that."))))
