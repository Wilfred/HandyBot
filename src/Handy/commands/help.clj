(ns Handy.commands.help)

;; todo: get description from project.clj
(defn help [{nick :nick}]
  "Print basic information about the bot."
  (let [version (System/getProperty "Handy.version")]
    (format "This is HandyBot %s, the self-documenting IRC bot. More docs are coming." version)))



