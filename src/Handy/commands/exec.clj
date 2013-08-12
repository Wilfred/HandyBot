(ns Handy.commands.exec
  (:require [clj-http.client :as client])
  (:use [clojure.data.json :only (read-json json-str)]
        [clojure.string :only (blank? trim join)]
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
  (call-json-rpc
   "ideone.com"
   "/api/1/service.json"
   method
   (into [(:ideone-user settings) (:ideone-password settings)] params)))

(defn json-rpc-result [response]
  "Given a raw HTTP reponse map from a JSON-RPC server, return the result map."
  (:result (read-json (:body response))))

(defn ideone-test-function []
  (json-rpc-result (call-ideone-rpc "testFunction" [])))

(defn ideone-get-languages
  "Query Ideone for the languages supported, returning a map of
language numbers to language names."
  []
  (let [raw-result (json-rpc-result (call-ideone-rpc "getLanguages" []))
        ;; map of form {:104 "AWK (gawk) (gawk-3.1.6)", ...}
        raw-languages (:languages raw-result)
        ;; converts :104 to 104
        symbol-to-int (comp #(Integer/parseInt %) name)
        language-pairs (map (fn [[symbol lang-name]] [(symbol-to-int symbol) lang-name])
                            raw-languages)]
    (apply hash-map (flatten language-pairs))))

(defn ideone-execute-code 
  "Run code SOURCE in language LANGUAGE-ID on Ideone. Synchronous."
  [language-id source]
  (let [input ""
        run true
        private true
        link (:link (json-rpc-result
                     (call-ideone-rpc
                      "createSubmission"
                      [source language-id input run private])))]
    (while (not (ideone-submission-is-finished link))
      (Thread/sleep 1000))
    link))

(def PROGRAM-FINISHED-STATUS 0)

(defn ideone-submission-is-finished [link]
  (= PROGRAM-FINISHED-STATUS
     (:status (json-rpc-result
               (call-ideone-rpc
                "getSubmissionStatus"
                [link])))))

(defn ideone-get-submission-output [link]
  (let [return-source false
        return-input false
        return-output true
        return-stderr true
        return-compilation true
        raw-result (json-rpc-result
                    (call-ideone-rpc
                     "getSubmissionDetails"
                     [link return-source return-input return-output
                      return-stderr return-compilation]))]
    {:stdout (:output raw-result) :stderr (:stderr raw-result)
     :compilation (:cmpinfo raw-result)}))

(defn python [{source :argument}]
  (let [python 4
        link (ideone-execute-code python source)
        output (ideone-get-submission-output link)
        stdout (:stdout output)
        stderr (:stderr output)]
    (str
     (if (blank? stdout) "" (format "[stdout] %s\n" (trim stdout)))
     (if (blank? stderr) "" (format "[stderr] %s\n" (trim stderr)))
     (format "[link] http://ideone.com/%s" link))))

(defn python3 [{source :argument}]
  (let [python3 116
        link (ideone-execute-code python3 source)
        output (ideone-get-submission-output link)
        stdout (:stdout output)
        stderr (:stderr output)]
    (str
     (if (blank? stdout) "" (format "[stdout] %s\n" (trim stdout)))
     (if (blank? stderr) "" (format "[stderr] %s\n" (trim stderr)))
     (format "[link] http://ideone.com/%s" link))))

(defn js
  "Run the JavaScript under node.js in IDEOne's sandbox."
  [{source :argument}]
  (let [js-node 56
        link (ideone-execute-code js-node source)
        output (ideone-get-submission-output link)
        stdout (:stdout output)
        stderr (:stderr output)]
    (str
     (if (blank? stdout) "" (format "[stdout] %s\n" (trim stdout)))
     (if (blank? stderr) "" (format "[stderr] %s\n" (trim stderr)))
     (format "[link] http://ideone.com/%s" link))))

(defn languages [{}]
  "Return a list of all the languages supported by %exec."
  (let [language-map (ideone-get-languages)
        language-list (map (fn [[id name]] (format "%s %s" id name)) (sort language-map))]
    (join "\n" language-list)))
