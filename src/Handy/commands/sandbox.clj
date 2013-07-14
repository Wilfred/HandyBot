(ns Handy.commands.sandbox
  (:use [clojail.core :only [sandbox safe-read]]
        [clojail.testers :only [secure-tester]]))

(defn eval-in-sandbox
  "Eval the given clojure in a sandbox, timing out after 5 seconds."
  [string]
  (let [sb (sandbox secure-tester :init '(def foo 1))]
    (sb (safe-read string))))
