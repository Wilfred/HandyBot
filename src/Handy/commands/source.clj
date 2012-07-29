(ns Handy.commands.source
  (:require [clojure.repl])
  (:use [Handy.routes :only [routes]]))

(defn source [{command-name :argument}]
  "Return the source code for a command."
  (let [command-name (subs command-name 1) ; strip leading %
        command (@routes command-name)]
    (if command
      (with-out-str (clojure.repl/source command))
        "That doesn't look like a command name to me. Try '%source %help'")))