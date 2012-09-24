(ns Handy.commands.help
  (:require [clojure.repl])
  (:use [clojure.string :only (blank?)]))

(defn find-matching-command [message]
  "We manually resolve rather than using (use ...) to avoid circular imports"
  ((ns-resolve 'Handy.patterns 'find-matching-command) message))

(defn source [{command-name :argument}]
  "Return the source code for a command."
  (let [command (find-matching-command command-name)]
    (if command
      (with-out-str (clojure.repl/source command))
        "That doesn't look like a command name to me. Try '%source %help'")))

(defn help
  "Gives help on all commands from their docstrings."
  [{command-name :argument}]
  (let [command (find-matching-command command-name)]
    (if command
      "Sorry, I can't give help on specific commands yet."
      "Hi, I'm a bot. Try %about or %help <command> (to be implemented).")))

;; todo: get description from project.clj
(defn about
  "Gives basic information about the current bot instance."
  [{}]
  (let [version (System/getProperty "Handy.version")]
    (format "This is HandyBot %s, the self-documenting IRC bot. Source lives at https://github.com/Wilfred/HandyBot."
            version)))