(ns Handy.commands.source
  (:require [clojure.repl]))

;; todo: we should use resolve for the patterns list instead
(def find-matching-command
  "We manually resolve rather than using (use ...) to avoid circular imports"
  (resolve 'Handy.patterns/find-matching-command))

(defn source [{command-name :argument}]
  "Return the source code for a command."
  (let [command (find-matching-command command-name)]
    (if command
      (with-out-str (clojure.repl/source command))
        "That doesn't look like a command name to me. Try '%source %help'")))