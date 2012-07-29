(ns Handy.core
  (:use [Handy.connection :only [connect-to-server server-event-loop]])
  (:use [Handy.settings :only [settings]]))

(defn -main []
  (let [server-connection
        (connect-to-server (:server settings) (:port settings))]
    (server-event-loop server-connection (:channel settings))
    (while true
      (println (.readLine (:in server-connection))))
    (.close (:in server-connection))))

;; code for playing with the bot:
;; (def s (connect-to-server IRC-SERVER PORT))
;; (server-event-loop s CHANNEL)
;; (say-in-channel s CHANNEL "foo")
;; (disconnect-from-server s)
