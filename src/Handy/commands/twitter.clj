(ns Handy.commands.twitter
  (:use [twitter.api.restful :only [statuses-user-timeline]]
        [twitter.oauth :only [make-oauth-creds]]
        [Handy.settings :only [settings]]))

(def twitter-credentials
  (make-oauth-creds
   (:twitter-app-key settings)
   (:twitter-app-secret settings)
   (:twitter-user-token settings)
   (:twitter-user-token-secret settings)))

(defn get-tweets
  "Get the last 20 tweet for this user."
  [username]
  (map :text
       (:body
        (statuses-user-timeline
         :oauth-creds twitter-credentials
         :params {:screen-name username
                  :count 20}))))

;; FIXME: there's a lot of duplication here
(defn devops [{}]
  (try
    (let [tweets (get-tweets "DEVOPS_BORAT")]
      (first (shuffle tweets)))
    ;; ideally, we'd catch a twitter exception, but the library doesn't define its own exception
    (catch Exception e (format "Twitter said '%s', sorry." e))))

(defn mongo [{}]
  (try
    (let [tweets (get-tweets "mongodbfacts")]
      (first (shuffle tweets)))
    ;; ideally, we'd catch a twitter exception, but the library doesn't define its own exception
    (catch Exception e (format "Twitter said '%s', sorry." e))))
