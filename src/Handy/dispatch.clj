(ns Handy.dispatch
  (:use [Handy.parsing :only [parse-bot-message]]
        [Handy.patterns :only [patterns]]
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

;; todo: skip excessive parsing
(defn dispatch-command [server-connection raw-message]
  (when-let [parsed-message (parse-bot-message raw-message)]
    (let [message (:message parsed-message)
          matching-patterns (filter (fn [[pattern _]] (re-find pattern message)) @patterns)
          matching-commands (map (fn [[pattern command]] command) matching-patterns)
          command (first matching-commands)]
      (when command
        (call-say-command server-connection command parsed-message)))))
