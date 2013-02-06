(ns Handy.commands.twitter
  (:require [twitter]))

(defn devops [{}]
  (let [tweets (twitter/user-timeline :screen-name "DEVOPS_BORAT")
        tweet (first (shuffle tweets))]
    (:text tweet)))

(defn twitter [{}])

