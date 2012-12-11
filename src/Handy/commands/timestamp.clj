(ns Handy.commands.timestamp
  (:use [Handy.settings :only [settings]]
        [clj-time.format :only [formatters unparse]]
        [clj-time.coerce :only [from-long]]))

(import 'org.ocpsoft.pretty.time.PrettyTime)

(defn timestamp
  "Decode a number in Unix time (milliseconds since 1970)."
  ;; TODO: seconds too
  [{raw-timestamp :argument}]
  (let [date (from-long (read-string raw-timestamp))
        absolute-date (unparse (formatters :rfc822) date)
        relative-date-format #(.format (new PrettyTime) (.toDate %))
        relative-date (relative-date-format date)]
    (format "%s (%s)" absolute-date relative-date)))