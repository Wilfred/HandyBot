(ns Handy.core)

(def IRC-SERVER "irc.freenode.net")
(def PORT 6667)

;; todo: automatically generate names when there's a name clash
(def NICK "HandyBot")
(def USER-NAME "HandyBot")
(def HOST-NAME "HandyBot")
(def SERVER-NAME "HandyBot")
(def REAL-NAME "Handy IRC Bot")

(def CHANNEL "#HandyBot")

(defn open-socket [host port]
  (let [socket (java.net.Socket. host port)
        in (java.io.BufferedReader. 
            (java.io.InputStreamReader. (.getInputStream socket)))
        out (java.io.PrintWriter. (.getOutputStream socket))]
    {:in in :out out}))

;; todo: format message automatically rather than requiring use of 'format
(defn send-to-server [server-connection raw-message]
  (let [out (:out server-connection)]
    (.print out (format "%s\r\n" raw-message))
    (.flush out)))

(defn connect-to-server [host port]
  (let [server-connection (open-socket host port)]
    (send-to-server server-connection (format "NICK %s" NICK))
    (send-to-server server-connection
                    (format "USER %s %s %s :%s"
                            USER-NAME HOST-NAME SERVER-NAME REAL-NAME))
    server-connection))

(defn disconnect-from-server [server-connection]
  (.close (:in server-connection)))

(defn join-channel [server-connection channel]
  (send-to-server server-connection (format "JOIN %s" channel)))

(defn say-in-channel [server-connection channel message]
  (send-to-server server-connection (format "PRIVMSG %s :%s" channel message)))

;; todo: nicely separate out raw functions from chat functions
(defn answer-ping [message]
  "Respond to a ping from the IRC server so we don't get disconnected."
  (format "PONG%s" (.substring message 4)))

;; todo: rename in and out to from-server and to-server
;; todo: proper logging
(defn idle-in-channel [server-connection channel]
  (join-channel server-connection channel)
  (while true
    (let [message (.readLine (:in server-connection))]
      (if (not (nil? message))
        (do
         (if (.startsWith message "PING")
           (send-to-server server-connection (answer-ping message)))
         (println message))))))

(defn -main []
  (let [server-connection (connect-to-server IRC-SERVER PORT)]
    (join-channel server-connection CHANNEL)
    (say-in-channel server-connection CHANNEL "hello there!")
    (.close (:in server-connection))))
