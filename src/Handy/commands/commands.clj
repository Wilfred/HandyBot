(ns Handy.commands.commands)

(defn get-routing
  "We manually resolve rather than using (use ...) to avoid circular imports"
  []
  @(var-get (ns-resolve 'Handy.routing 'routes)))

(defn commands
  "List all the commands available."
  [{}]
  (let [patterns (map (fn [[pattern command]] pattern) (get-routing))]
    (str "I respond to IRC messages that start with: \n"
         (apply str
                (interpose " " (sort patterns))))))
