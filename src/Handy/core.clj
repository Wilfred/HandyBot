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

(defn -main []
  (let [server-connection (connect-to-server IRC-SERVER PORT)]
    (send-to-server server-connection (format "JOIN %s" CHANNEL))
    (send-to-server server-connection (format "PRIVMSG %s :%s" CHANNEL "hello there!"))
    (.close (:in server-connection))))