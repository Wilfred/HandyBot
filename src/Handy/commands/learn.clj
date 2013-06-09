(ns Handy.commands.learn
  (:use [clojure.string :only [split join]]))

(defn get-routes
  "We manually resolve rather than using (use ...) to avoid circular imports"
  []
  (var-get (ns-resolve 'Handy.routing 'routes)))

(defn startswith?
  "Return true if the first characters of S are PREFIX."  
  [s prefix]
  (.startsWith s prefix))

(defn learn
  "Add a new command to the routes, which just returns the string specified."
  [{name-and-response :argument}]
  (let [name (first (split name-and-response #" "))
        response-words (rest (split name-and-response #" "))
        response (join " " response-words)
        routes (get-routes)]
    (cond
     (not (startswith? name "%"))
     "Commands need to start with %."
     (empty? response)
     "You need to specify a response for this new command. E.g. %learn %robot Beware robot domination!"
     :else
     (do
       (dosync (alter routes assoc name response))
       "Duly noted."))))
