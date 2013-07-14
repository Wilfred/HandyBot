(ns Handy.settings)

(try
  (use 'Handy.private-settings)
  (catch java.io.FileNotFoundException e
    (do
      (def private-settings {})
      (prn "Warning: No private settings found!"))))

;; todo: automatically generate names when there's a name clash
(def settings
  {:nick "HandyBot"
   :user-name "HandyBot"
   :host-name "HandyBot"
   :server-name "HandyBot"
   :real-name "Handy IRC Bot"
   :server "irc.freenode.net"
   :port 6667
   :channel "#HandyBot"
   :ideone-user (:ideone-user private-settings)
   :ideone-password (:ideone-password private-settings)
   :lastfm-api-key (:lastfm-api-key private-settings)
   :twitter-app-key (:twitter-app-key private-settings)
   :twitter-app-secret (:twitter-app-secret private-settings)
   :twitter-user-token (:twitter-user-token private-settings)
   :twitter-user-token-secret (:twitter-user-token-secret private-settings)})
