(ns Handy.commands.source
  (:require [clojure.repl]))

(defn find-matching-command [message]
  "We manually resolve rather than using (use ...) to avoid circular imports"
  ((ns-resolve 'Handy.patterns 'find-matching-command) message))

(defn source [{command-name :argument}]
  "Return the source code for a command."
  (let [command (find-matching-command command-name)]
    (if command
      (with-out-str (clojure.repl/source command))
        "That doesn't look like a command name to me. Try '%source %help'")))