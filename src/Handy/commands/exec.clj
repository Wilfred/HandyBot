(ns Handy.commands.exec
  (:require [clj-http.client :as client])
  (:use [clojure.data.json :only (read-json json-str)]
        [Handy.settings :only [settings]]))

(defn current-unix-time [] (.getTime (java.util.Date.)))

(defn call-json-rpc [domain path method params]
  "Call METHOD with arguments PARAMS on a JSON-RPC server."
  (let [url (format "http://%s%s" domain path)]
    (client/post
     url
     {:body (json-str
             {"method" method
              "params" params
              "id" (current-unix-time)})})))

;; TODO: exception for rpc failing
;; TODO: don't require callers to pass in username and password
(defn call-ideone-rpc [method params]
  (call-json-rpc "ideone.com" "/api/1/service.json" method params))

(defn json-rpc-result [response]
  "Given a raw HTTP reponse map from a JSON-RPC server, return the result map."
  (:result (read-json (:body response))))

(def USER "handybot")
(def PASSWORD "590b39c05cafe")

(defn ideone-test-function []
  (json-rpc-result (call-ideone-rpc
                    "testFunction"
                    [(:ideone-user settings)
                     (:ideone-password settings)])))

(defn ideone-get-languages []
  "Query Ideone for the languages supported, returning a map of language IDs
to language names."
  (:languages (json-rpc-result (call-ideone-rpc
                                "getLanguages"
                                [(:ideone-user settings)
                                 (:ideone-password settings)]))))

(defn parse-language-name [language-with-version]
  "Given a string of the form \"foo bar (1.0)\", separate the language name from
the version."
  (let [[whole-message name version] (re-find #"(.*) \((.*)\)" language-with-version)]
    {:name name :version version}))

(defn languages [{}]
  "Return a list of all the languages supported by %exec."
  (let [languages-with-versions (sort (vals (ideone-get-languages)))
        language-names (map :name (map parse-language-name languages-with-versions))]
    (apply str (interpose ", " language-names))))