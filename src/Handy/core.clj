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
(defn send-to-server [socket message]
  (let [out (:out socket)]
    (.print out (format "%s\r\n" message))
    (.flush out)))

(defn server-connection [host port]
  (let [server-socket (open-socket host port)]
    (send-to-server server-socket (format "NICK %s" NICK))
    (send-to-server server-socket
                    (format "USER %s %s %s :%s"
                            USER-NAME HOST-NAME SERVER-NAME REAL-NAME))
    server-socket))

(defn -main []
  (let [server-conn (server-connection IRC-SERVER PORT)]
    (send-to-server server-conn (format "JOIN %s" CHANNEL))
    (send-to-server server-conn (format "PRIVMSG %s :%s" CHANNEL "hello there!"))
    (.close (:in server-conn))))

