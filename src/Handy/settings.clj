(ns Handy.settings
  (:use [Handy.private-settings :only [private-settings]]))

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
   :ideone-password (:ideone-password private-settings)})