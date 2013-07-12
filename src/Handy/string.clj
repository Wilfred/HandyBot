(ns Handy.string
    (:use [clojure.string :only [triml]]))

(defn startswith?
  "Return true if the first characters of S are PREFIX."  
  [s prefix]
  (and s
       (.startsWith s prefix)))

(defn char-indexes
  "Return a list of all the positions of CHAR in STRING."
  [string char]
  (let [enumerated-list (map list string (range))
        matching-chars (filter (fn [[element _]] (= element char)) enumerated-list)
        indexes (map (fn [[_ index]] index) matching-chars)]
    indexes))

(defn word-substring
  "Split STRING into two components, where the first is no longer
than LENGTH. We try to break on a word boundary."
  [string length]
  (let [space-positions (char-indexes string \space)
        split-positions (filter (fn [x] (< x length)) space-positions)
        split-position (if (empty? split-positions) length (last split-positions))]
    (if (< (count string) length) [string ""]
        [(subs string 0 split-position) (triml (subs string split-position))])))

(defn split-words
  "Split STRING on word boundaries, into substrings no longer than MAX-LENGTH."
  [string max-length]
  (let [[first-part remainder] (word-substring string max-length)]
    (if (empty? remainder) [first-part]
        (into [first-part] (split-words remainder max-length)))))
