(ns Handy.commands.fashion
  (:use [clojure.string :only [split-lines lower-case]]))


(def colours (map lower-case (split-lines (slurp "colours.txt"))))

(def clothing (split-lines (slurp "clothing.txt")))

(def intros ["I think you'd look great in"
             "You know what would work for you? I suggest"
             "I suggest"
             "Consider"
             "I think your best option is"])

(def combiners ["paired with"
                "combined with"
                "plus"
                "and"
                "together with"
                "tastefully paired with"
                "in conjuction with"
                "along with"])

(defn suggest
  "Suggest a random pairing of clothing items in a random colour."
  [{}]
  (let [[item1 item2] (take 2 (shuffle clothing))
        [colour1 colour2] (take 2 (shuffle colours))
        item1-article (if (= (last item1) \s) "" "a ")
        item2-article (if (= (last item2) \s) "" "a ")
        intro (first (shuffle intros))
        combiner (first (shuffle combiners))]
    (format "%s %s%s %s %s %s%s %s."
            intro item1-article colour1 item1 combiner item2-article colour2 item2)))