(ns Handy.commands.exec
  (:require [clj-http.client :as client])
  (:use [clojure.data.json :only (read-json json-str)]
        [clojure.string :only (blank? trim)]
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

(defn ideone-get-languages []
  "Query Ideone for the languages supported, returning a map of language IDs
to language names."
  (:languages (json-rpc-result (call-ideone-rpc
                                "getLanguages"
                                []))))

(defn ideone-execute-code [language-id source]
  (let [input ""
        run true
        private true]
    (:link (json-rpc-result
            (call-ideone-rpc
             "createSubmission"
             [source language-id input run private])))))

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
        output (do
                 (Thread/sleep 2000) ; fixme -- should poll ideone-submission-is-finished
                 (ideone-get-submission-output link))
        stdout (:stdout output)
        stderr (:stderr output)]
    (str
     (if (blank? stdout) "" (format "[stdout] %s\n" (trim stdout)))
     (if (blank? stderr) "" (format "[stderr] %s\n" (trim stderr)))
     (format "[link] http://ideone.com/%s" link))))

(defn js [{source :argument}]
  (let [js-spidermonkey 112
        link (ideone-execute-code js-spidermonkey source)
        output (do
                 (Thread/sleep 2000) ; fixme -- should poll ideone-submission-is-finished
                 (ideone-get-submission-output link))
        stdout (:stdout output)
        stderr (:stderr output)]
    (str
     (if (blank? stdout) "" (format "[stdout] %s\n" (trim stdout)))
     (if (blank? stderr) "" (format "[stderr] %s\n" (trim stderr)))
     (format "[link] http://ideone.com/%s" link))))

(defn languages [{}]
  "Return a list of all the languages supported by %exec."
  (str (ideone-get-languages)))