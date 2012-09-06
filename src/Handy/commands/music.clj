(ns Handy.commands.music
  (:import (java.io ByteArrayInputStream))
  (:require [clj-http.client :as client])
  (:use [clojure.data.json :only (read-json)]))

;; todo: actual api key, not just copy-paste from the docs sample code
(def api-key "b25b959554ed76058ac220b7b2e0a026")

(defn last-fm-similar-artists [artist-name]
  (let [json-response
        (:body (client/get "http://ws.audioscrobbler.com/2.0/"
                           {:throw-exceptions false
                            :query-params {"method" "artist.getsimilar"
                                           "artist" artist-name
                                           "api_key" api-key
                                           "format" "json"}}))
        parsed-response (read-json json-response)
        artists (:artist (:similarartists parsed-response))
        artists-names (map :name artists)]
    artists-names))

(defn music
  "Suggest similar artists to the one given."
  [{artist-name :argument}]
  (let [similar-artists (last-fm-similar-artists artist-name)]
    (if (not-empty similar-artists)
      (format "Hmm, if you like %s, you might like %s, %s or even %s."
              artist-name
              (nth similar-artists 0 "")
              (nth similar-artists 1 "")
              (nth similar-artists 2 ""))
      (format "Sorry, I haven't heard of %s." artist-name))))