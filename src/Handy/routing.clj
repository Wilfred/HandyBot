(ns Handy.routing
  (:use [Handy.commands.help :only [help about source]]
        [Handy.commands.magic8 :only [magic8]]
        [Handy.commands.exec :only [languages python js]]
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

(def routing
  {"%hello" hello
   "%more" more
   "%help" help
   "%about" about
   "%source" source
   "%magic8" magic8
   "%languages" languages
   "%python" python
   "%js" js
   "%commands" commands
   "%parse" parse
   "%wisdom" wisdom
   "%music" music
   "%time" timestamp
   "%fashion" suggest})

(defn find-matching-command
  "Given a command name, find the first matching command,
unknown-command, or return nil." ; TODO: cleaner separation of unknown-command
  [command-name]
  (or (routing command-name)
      (if (startswith? command-name "%") unknown-command)))