(ns Handy.interaction
  "Functions that make the bot speak in a channel."
  (:use [clojure.string :only [trim]]))

;; todo: factor out the bot command prefix

;; todo: fix the duplication with connection.clj of send-to-server
(defn send-to-server [server-connection raw-message]
  (let [to-server (:to-server server-connection)]
    (.print to-server (format "%s\r\n" raw-message))
    (.flush to-server)))

(defn say-in-channel [server-connection channel message]
  (send-to-server server-connection (format "PRIVMSG %s :%s" channel message)))

(defn parse-channel-message [raw-message]
  "Separate RAW-MESSAGE into a map of the different parts: the
message, the user, etc."
  (let [[whole-message nick user host message-type channel message]
        (re-find #":(.*?)!(.*?)@(.*?) (.*?) (.*?) :(.*)" raw-message)]
    {:nick nick :user user :host host :message-type message-type
     :channel channel :message message}))

(defn parse-command [message]
  "Separate MESSAGE into the command name and its argument."
  (when-let [match (re-find #"%([^ ]+)(.*)" message)]
    (let [[_ command-name argument] match]
      {:command-name command-name :argument (trim argument)})))

(defn say-hello [{nick :nick}]
  "Greet the user who spoke."
  (format "Hello %s!" nick))

(defn dispatch-command [server-connection raw-message]
  (let [parsed-message (parse-channel-message raw-message)
        parsed-command (parse-command (parsed-message :message))]
    (when parsed-command
      (if (= (parsed-command :command-name) "hello")
        (say-in-channel server-connection (parsed-message :channel) (say-hello parsed-message))
        (say-in-channel server-connection (parsed-message :channel) "Sorry, I don't know how to do that.")))))
