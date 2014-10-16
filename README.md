porter-stemmer
==============

http://tartarus.org/martin/PorterStemmer/def.txt

## Determining word measure.

[C]VC{m}[V]

```
(def C "[^aeiou][^aeiouy]*")

(def V "[aeiouy][aeiou]*")

(def m0 (re-pattern (str "(" C ")?(" V ")?")))  

(def m1 (re-pattern (str "(" C ")?" V C "(" V ")?")))

; generalize to m_N

(defn m [n] 
  (re-pattern (str "(" C ")?"
              (clojure.string/join (take n (repeat (str V C))))
               "(" V ")?")))
               
(= (str m0) (str (m 0)))
(= (str m1) (str (m 1)))
```


** http://clojuredocs.org/clojure.core/re-find

non capturing regex look behind

(re-seq #"((?:o)(y))" "toy")
> (["oy" "oy" "y"])

