(ns Handy.commands.more
  (:use [clojure.string :only [split join blank?]]))

(def remaining-output (ref ""))

(defn get-more-output
  "Get another five lines of output from the last command."
  []
  (dosync
   (let [remainder @remaining-output
         lines (split remainder #"\n")
         more-output (join "\n" (take 5 lines))
         new-remainder (join "\n" (drop 5 lines))]
     (ref-set remaining-output new-remainder)
     more-output)))

(defn set-command-output
  "Save the output of this command so we can access it incrementally"
  [output]
  (dosync (ref-set remaining-output (or output ""))))

(defn more
  "If the last command returned too much output, return the next bit of output."
  [{}]
  @remaining-output)
