(ns Handy.patterns
  (:use [Handy.commands.help :only [help]]
        [Handy.commands.source :only [source]]
        [Handy.commands.magic8 :only [magic8]]
        [Handy.commands.exec :only [languages python js]]
        [Handy.commands.hello :only [hello]]
        [Handy.commands.commands :only [commands]]
        [Handy.commands.parse :only [parse]]
        [Handy.commands.wisdom :only [wisdom]]))

;; todo: move to a separate file
;; todo: suggest similarly spellt commands
(defn unknown-command [{}]
  "Reponse given when we don't have any command matching the user's request."
  "Sorry, I don't know how to do that. Try %commands.")

(def patterns
  "Regular expressions that will be tried, in order, to execute a bot command."
  (ref [[#"^%hello" hello]
        [#"^%help" help]
        [#"^%source" source]
        [#"^%magic8" magic8]
        [#"^%languages" languages]
        [#"^%python" python]
        [#"^%js" js]
        [#"^%commands" commands]
        [#"^%parse" parse]
        [#"^%wisdom" wisdom]
        [#"^%.+" unknown-command]
        ]))

(defn find-matching-command [message]
  "Given a IRC message, iterate through our command patterns and find the
first matching command, or return nil."
  (let [matching-patterns (filter (fn [[pattern _]] (re-find pattern message)) @patterns)
        matching-commands (map (fn [[pattern command]] command) matching-patterns)
        command (first matching-commands)]
    command))