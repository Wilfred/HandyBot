(ns Handy.commands.twitter
  (:require [twitter]))

(defn devops [{}]
  (try
    (let [tweets (twitter/user-timeline :screen-name "DEVOPS_BORAT")
          tweet (first (shuffle tweets))]
      (:text tweet))
    ;; ideally, we'd catch a twitter exception, but the library doesn't define its own exception
    (catch Exception e (format "Twitter said '%s', sorry." e))))
