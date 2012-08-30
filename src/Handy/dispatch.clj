(ns Handy.dispatch
  (:use [Handy.parsing :only [parse-bot-message]]
        [Handy.patterns :only [find-matching-command]]
        [clojure.string :only [split-lines triml]]))

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

(defn char-indexes
  "Return a list of all the positions of CHAR in STRING."
  [string char]
  (let [enumerated-list (map list string (range))
        matching-chars (filter (fn [[element _]] (= element char)) enumerated-list)
        indexes (map (fn [[_ index]] index) matching-chars)]
    indexes))

(defn word-substring
  "Split STRING into two components, where the first is no longer
than LENGTH. We try to break on a word boundary."
  [string length]
  (let [space-positions (char-indexes string \space)
        split-positions (filter (fn [x] (< x length)) space-positions)
        split-position (if (empty? split-positions) length (last split-positions))]
    (if (< (count string) length) [string ""]
        [(subs string 0 split-position) (triml (subs string split-position))])))

(defn split-words
  "Split STRING on word boundaries, into substrings no longer than MAX-LENGTH."
  [string max-length]
  (let [[first-part remainder] (word-substring string max-length)]
    (if (empty? remainder) [first-part]
        (into [first-part] (split-words remainder max-length)))))

(defn split-irc-lines
  "Split a newline separated string into a list of strings, line
breaking excessively long lines."
  [raw-text]
  (flatten (map (fn [t] (split-words t 100)) (split-lines raw-text))))

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
