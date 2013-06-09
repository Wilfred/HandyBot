(ns Handy.routing
  (:use [Handy.commands.help :only [help about source]]
        [Handy.commands.magic8 :only [magic8]]
        [Handy.commands.exec :only [languages python python3 js]]
        [Handy.commands.twitter :only [devops]]
        [Handy.commands.hello :only [hello]]
        [Handy.commands.more :only [more]]
        [Handy.commands.commands :only [commands]]
        [Handy.commands.parse :only [parse]]
        [Handy.commands.wisdom :only [wisdom]]
        [Handy.commands.timestamp :only [timestamp]]
        [Handy.commands.fashion :only [suggest]]
        [Handy.commands.music :only [music]]))

;; todo: move to a separate file
;; todo: suggest similarly spellt commands
(defn unknown-command
  "Reponse given when we don't have any command matching the user's request."
  [{}]
  "Sorry, I don't know how to do that. Try %commands.")

(defn startswith?
  "Return true if the first characters of S are PREFIX."  
  [s prefix]
  (.startsWith s prefix))

(def routes
  (ref
   {"%hello" hello
    "%more" more
    "%help" help
    "%about" about
    "%source" source
    "%magic8" magic8
    "%languages" languages
    "%python" python
    "%python3" python3
    "%js" js
    "%devops" devops
    "%commands" commands
    "%parse" parse
    "%wisdom" wisdom
    "%music" music
    "%time" timestamp
    "%fashion" suggest}))

(defn bot-command?
  "Is this a message to HandyBot?"
  [command-name]
  (startswith? command-name "%"))

(defn find-matching-command
  "Given a command name, find the first matching command,
unknown-command, or return nil." ; TODO: cleaner separation of unknown-command
  [command-name]
  (when (bot-command? command-name)
    (let [matching-command (@routes command-name)]
      (cond
       ;; treat strings in the routes as functions that return the string
       (= (type matching-command) java.lang.String)
       ;; all HandyBot commands need to take a map
       (fn [{}] matching-command)
       (= matching-command nil)
       unknown-command
       :else matching-command))))
