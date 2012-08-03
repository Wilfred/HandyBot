(ns Handy.routing
  "Functions that make the bot speak in a channel."
  (:use [Handy.parsing :only [parse-bot-message]]
        [Handy.commands.help :only [help]]
        [Handy.commands.source :only [source]]
        [Handy.commands.magic8 :only [magic8]]
        [Handy.commands.exec :only [languages]]
        [Handy.commands.hello :only [hello]]
        [Handy.routes :only [routes]]
        [clojure.string :only [split-lines]]))

;; todo: fix the duplication with connection.clj of send-to-server and join-channel
(defn send-to-server [server-connection raw-message]
  (let [to-server (:to-server server-connection)]
    (.print to-server (format "%s\r\n" raw-message))
    (.flush to-server)))

(defn say-in-channel [server-connection channel message]
  (send-to-server server-connection (format "PRIVMSG %s :%s" channel message)))

(defn call-raw-command [server-connection command parsed-message]
  "Execute a bot command COMMAND that was called by
PARSED-MESSAGE. The command may send any IRC command to the server."
  (send-to-server server-connection (command parsed-message)))

(defn call-say-command [server-connection command parsed-message]
  "Execute a bot command COMMAND that was called by
PARSED-MESSAGE. The command may only say something in the channel."
  (let [command-output (command parsed-message)]
    (doseq [line (split-lines command-output)]
      (say-in-channel server-connection (parsed-message :channel) line))))

(dosync (alter routes conj
               {"hello" hello
                "help" help
                "source" source
                "magic8" magic8
                "languages" languages}))

(defn unknown-command [{}]
  "Reponse given when we don't have any command matching the user's request."
  (str "Sorry, I don't know how to do that. I know: " (keys @routes)))

(defn dispatch-command [server-connection raw-message]
  (when-let [parsed-message (parse-bot-message raw-message)]
    (let [command-name (parsed-message :command-name)
          command (or (@routes command-name) unknown-command)]
     (call-say-command server-connection command parsed-message))))
