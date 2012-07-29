(ns Handy.routing
  "Functions that make the bot speak in a channel."
  (:use [clojure.string :only [trim]]
         [Handy.commands.help :only [help]]))

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

(defn call-raw-command [server-connection command parsed-message]
  "Execute a bot command COMMAND that was called by
PARSED-MESSAGE. The command may send any IRC command to the server."
  (send-to-server server-connection (command parsed-message)))

(defn call-say-command [server-connection command parsed-message]
  "Execute a bot command COMMAND that was called by
PARSED-MESSAGE. The command may only say something in the channel."
  (say-in-channel server-connection (parsed-message :channel) (command parsed-message)))

(def command-routing
  "A map of command names (as used by users) to their respective functions."
  (ref {}))

(defmacro defcommand
  "A command available in the IRC bot with the %foo syntax."
  [name params doc-string body]
  `(do
     (defn ~name ~params ~doc-string ~body)
     (dosync (alter command-routing conj
                    {(str '~name) ~name}))))

(defcommand hello [{nick :nick}]
  "Greet the user who spoke."
  (format "Hello %s!" nick))

(defn join-channel [{channel :argument}]
  (format "JOIN %s" channel))

(defcommand magic8 [{}]
  "Randomly choose a yes-no-maybe response."
  (rand-nth ["Yes." "No." "Definitely." "Of course not!" "Highly likely."
              "Ask yourself, do you really want to know?"
              "I'm telling you, you don't want to know." "Mu!"]))

(dosync (alter command-routing conj
               {"help" help}))

(defn unknown-command [{}]
  "Reponse given when we don't have any command matching the user's request."
  (str "Sorry, I don't know how to do that. I know: " (keys @command-routing)))

(defn dispatch-command [server-connection raw-message]
  (when-let [parsed-message (parse-bot-message raw-message)]
    (let [command-name (parsed-message :command-name)
          command (or (@command-routing command-name) unknown-command)]
     (call-say-command server-connection command parsed-message))))
