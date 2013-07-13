(ns Handy.persist-commands)

;; fixme: this is duplicated code
(defn get-routing
  "We manually resolve rather than using (use ...) to avoid circular imports"
  []
  (var-get (ns-resolve 'Handy.routing 'routes)))

(defn serialize
  "Convert a clojure data type to a string that can be re-evaluated."
  [value]
  (binding [*print-dup* true] (pr-str value)))

(defn deserialize
  "Inverse of serialize."
  [string]
  (read-string string))

(defn save-custom-commands
  "Save all user-defined commands to a file."
  []
  (let [custom-commands
        (into {}
              (filter (fn [[key val]] (= (type val) String))
                      @(get-routing)))]
    (spit "custom_commands.clj" (serialize custom-commands))))

(defn load-custom-commands
  "Restore all user-defined commands from a file."
  []
  (let [routes (get-routing)
        custom-commands
        (deserialize (slurp "custom_commands.clj"))]
    (dosync (ref-set routes (merge custom-commands @routes)))))
