(ns Handy.dispatch
  (:use [Handy.parsing :only [parse-bot-message]]
        [Handy.patterns :only [find-matching-command]]
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

(defn split-str-by-length
  "Split STRING into a list of substrings, each no longer than MAX-LENGTH."
  [string max-length]
  (if (<= (count string) max-length) [string]
      (into [(subs string 0 max-length)]
            (split-str-by-length (subs string max-length) max-length))))

(defn split-irc-lines
  "Split a newline separated string into a list of strings, line
breaking excessively long lines."
  [raw-text]
  (flatten (map (fn [t] (split-str-by-length t 100)) (split-lines raw-text))))

(defn call-say-command [server-connection command parsed-message]
  "Execute a bot command COMMAND that was called by
PARSED-MESSAGE. The command may only say something in the channel."
  (let [command-output (command parsed-message)]
    (doseq [line (split-irc-lines command-output)]
      (say-in-channel server-connection (parsed-message :channel) line))))

;; todo: skip excessive parsing
(defn dispatch-command [server-connection raw-message]
  (when-let [parsed-message (parse-bot-message raw-message)]
    (let [message (:message parsed-message)
          command (find-matching-command message)]
      (when command
        (call-say-command server-connection command parsed-message)))))
