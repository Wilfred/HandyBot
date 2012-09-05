(ns Handy.commands.help)

(defn help
  "Gives help on all commands from their docstrings."
  [{command-name :argument}]
  "Hi, I'm a bot. Try %about or %help <command> (to be implemented).")

;; todo: get description from project.clj
(defn about
  "Gives basic information about the current bot instance."
  [{}]
  (let [version (System/getProperty "Handy.version")]
    (format "This is HandyBot %s, the self-documenting IRC bot. Source lives at https://github.com/Wilfred/HandyBot."
            version)))



