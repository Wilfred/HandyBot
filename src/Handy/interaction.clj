(ns Handy.interaction
  "Functions that make the bot speak in a channel.")

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

(defn say-hello [{nick :nick}]
  "Greet the user who spoke."
  (format "Hello %s!" nick))

(defn dispatch-command [server-connection raw-message]
  (let [parsed-message (parse-channel-message raw-message)]
    (if (.startsWith (parsed-message :message) "%")
      (say-in-channel server-connection (parsed-message :channel) (say-hello parsed-message)))))
