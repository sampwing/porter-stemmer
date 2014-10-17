(ns porter-stemmer.core
  (:gen-class))

(def C "[^aeiou][^aeiouy]*")

(def V "[aeiouy][aeiou]*")

(defn m-string [n] 
  (str "(" C ")?"
    (clojure.string/join (take n (repeat (str V C))))
   "(" V ")?"))

(defn mn-string [n]
  (str "(" C ")?"
       "(" V C "){" n ",}"
       "(" V ")?"))

(def not-nil? (complement nil?))

(defn star-s
  [word character before after]
  (let [match-pattern (re-pattern (str character before "$"))
        replace-pattern (re-pattern (str before "$"))]
    (if (not-nil? (re-find match-pattern word))
      (clojure.string/replace word replace-pattern after)
      word)))

(defn star-v-star
  [word before after]
  (let [match-pattern (re-pattern (str ".*" V ".*" before "$"))
        replace-pattern (re-pattern (str before "$"))]
    (prn match-pattern)
    (if (not-nil? (re-find match-pattern word))
      (clojure.string/replace word replace-pattern after)
      word)))

(defn rule-builder
  [{:keys [rules before after]}])

(defn count-match
  [word pattern]
  (count (re-find (re-pattern (str pattern "$")) word)))

(defn pick-rule
  [rules word]
  (let [sorted (sort #(compare 
                        (count-match word (:before %2))
                        (count-match word (:before %1)))
                     rules)]
  (first sorted)))

(def step1a-rules 
  [{:before "sses" :after "ss"}
   {:before "ies" :after "i"}
   {:before "ss" :after "ss"}
   {:before "s" :after "s"}])

(defn step1a 
  [word]
  (loop [n 0 {:keys [before after]} (nth step1a-rules 0)]
    (let [pattern (re-pattern (str before "$"))]
      (if (nil? (re-find pattern word))
        (recur (inc n) (nth step1a-rules n))
        (clojure.string/replace word pattern after)))))

(defn step1a-new
  [word]
  (let [{:keys [before after]} (pick-rule step1a-rules word)]
    (let [pattern (re-pattern (str before "$"))]
      (clojure.string/replace word pattern after))))

(def step1b23-rules
  [{:before "at" :after "ate"}
   {:before "bl" :after "ble"}
   {:before "iz" :after "ize"}])

(defn step1b23
  [word]
  (let [{:keys [before after]} (pick-rule step1b23-rules word)]
    (let [pattern (re-pattern (str before "$"))]
      (clojure.string/replace word pattern after))))

(def step1b-rules
  [{:rule (mn-string 1) :before "eed" :after "ee" :next-func nil}
   {:rule (str ".*" V ".*") :before "ed" :after "" :next-func step1b23}
   {:rule (str ".*" V ".*") :before "ing" :after "" :next-func step1b23}])


(defn step1b-new
  [word]
  (let [{:keys [rule before after next-func]} (pick-rule step1b-rules word)]
    (let [match-pattern (re-pattern (str rule before "$"))
          replace-pattern (re-pattern (str before "$"))]
      (if (not-nil? (re-find match-pattern word))
        (let [word (clojure.string/replace word replace-pattern after)]
          (if (nil? next-func)
            word
            (step1b23 word)))
      word))))

(defn step1b 
  [word]
  (loop [n 1 {:keys [before after rule]} (nth step1b-rules 0)]
    (let [match-pattern (re-pattern (str rule before "$"))
          replace-pattern (re-pattern (str before "$"))]
      (if (nil? (re-find match-pattern word))
        (if (= n (count step1b-rules))
          word
          (recur (inc n) (nth step1b-rules n)))
        (clojure.string/replace word replace-pattern after)))))

(defn pipeline
  [word]
  (-> word
      step1a-new
      step1b-new))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (prn (m-string 1))
  (prn (mn-string 1))
  (rule-builder {:rules nil :before "SS" :after "ES"})
  (prn (pick-rule step1b-rules "feed"))
  (prn (pipeline "conflated"))
  (doseq [string ["caresses" "ponies" "ties" "caress" "cats"]]
    (prn (str string " -> " (pipeline string))))
  (doseq [string ["feed" "agreed" "plastered" "bled" "monitoring" "sing"]]
    (prn (str string " -> " (pipeline string))))
  (prn (str "successes -> " (star-s "successes" \e "sses" "ss")))) 
