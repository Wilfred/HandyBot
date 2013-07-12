(ns Handy.dispatch
  (:use [Handy.parsing :only [parse-irc-message]]
        [Handy.routing :only [find-matching-command]]
        [Handy.commands.more :only [more set-command-output get-more-output remaining-output]]
        [clojure.string :only [split-lines]]
        [Handy.string :only [split-words]]))

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

(def MAX-LINE-LENGTH 160)
(defn split-irc-lines
  "Split a newline separated string into a list of strings, line
breaking excessively long lines."
  [raw-text]
  (flatten (map (fn [t] (split-words t MAX-LINE-LENGTH)) (split-lines raw-text))))

(defn call-say-command
  "Execute a bot command COMMAND that was called by
PARSED-MESSAGE. The command may only say something in the channel."
  [server-connection command parsed-message]
  (let [command-output (command parsed-message)]
    (set-command-output command-output)
    
    (doseq [line (split-irc-lines (get-more-output))]
      (say-in-channel server-connection (parsed-message :channel) line))
    (if (not (zero? (count @remaining-output)))
      (say-in-channel server-connection (parsed-message :channel) "(type %more for more output)"))))

(defn dispatch-command [server-connection raw-message]
  (when-let [parsed-message (parse-irc-message raw-message)]
    (let [command (find-matching-command (:command-name parsed-message))]
      (when command
        (call-say-command server-connection command parsed-message)))))
