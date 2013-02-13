(ns Handy.commands.twitter
  (:require [twitter]))

(defn devops [{}]
  ;; ideally, we'd catch a twitter exception, but the library doesn't define its own exception
  (let [tweets (try
                 (twitter/user-timeline :screen-name "DEVOPS_BORAT")
                 (catch Exception e ["Twitter said no, sorry."]))
        tweet (first (shuffle tweets))]
    (:text tweet)))
