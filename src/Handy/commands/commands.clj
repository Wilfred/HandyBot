(ns Handy.commands.commands)

(defn get-patterns
  "We manually resolve rather than using (use ...) to avoid circular imports"
  []
  (var-get (ns-resolve 'Handy.patterns 'patterns)))

(defn commands
  "List all the commands according to their regular expression."
  [{}]
  (str "I repond to IRC messages matching the following patterns: "
       (apply str
              (interpose " "
                         (map str
                              (map first @(get-patterns)))))))