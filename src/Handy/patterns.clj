(ns Handy.patterns
  (:use [Handy.commands.help :only [help]]
        [Handy.commands.source :only [source]]
        [Handy.commands.magic8 :only [magic8]]
        [Handy.commands.exec :only [languages]]
        [Handy.commands.hello :only [hello]]))

;; todo: move to a separate file
;; todo: suggest similarly spellt commands
(defn unknown-command [{}]
  "Reponse given when we don't have any command matching the user's request."
  "Sorry, I don't know how to do that. Try %commands.")

(def patterns
  "Regular expressions that will be tried, in order, to execute a bot command."
  (ref [[#"%hello" hello]
        [#"%help" help]
        [#"%source" source]
        [#"%magic8" magic8]
        [#"%languages" languages]
        [#"%.+" unknown-command]
        ]))
