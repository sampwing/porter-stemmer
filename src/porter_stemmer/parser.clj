(ns porter-stemmer.parser
  (:gen-class))

(use '[clojure.core.match :only (match)])
(require '[instaparse.core :as insta])

(def word-parser 
  ; definition for parsing words into [C](VCVC){n}[V] form
  (insta/parser 
    "S := [C] M [V] 
     C := #\"[^aeiou][^aeiouy]*\" 
     V := #\"[aeiouy][aeiou]*\"  
     m := V C | Epsilon
     M := M m | m" ))

(defn count-m
  [v]
  (count (filter #(= %1 :m) (clojure.core/flatten v))))

(defn parse
  [word]
  (let [[success f n l] (word-parser word)]
  (prn success f n l)
  (match [success f n l]
         [nil _ _ _] 0
         [_ [:M _] nil nil] {:m (count-m f)}
         [_ [:C _] _ _] {:m (count-m n)}
         :else 0)))  

(defn -main
  [& args]) 
