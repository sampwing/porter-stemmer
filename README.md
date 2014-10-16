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

(defn m-string [n] 
  (str "(" C ")?"
       (clojure.string/join (take n (repeat (str V C))))
       "(" V ")?"))

(defn m [n]
  (re-pattern (m-string n)))
               
(= (str m0) (str (m 0)))
(= (str m1) (str (m 1)))
```

## Conditions

### *S

```
(defn stem-ends-with [stem c]
  (= (last stem) c))
```

### *v*

```
(defn stem-has-vowel [stem]
  ((complement nil?) (re-find (re-pattern V) stem)))
```

### *d

```
tbd
```

### *o

```
(def cvc "[^aeiou][aeiou][^aeiouwxy]$")

(defn stem-ends-cvc [stem]
  ((complement nil?) (re-find (re-pattern cvc) stem)))
```

** http://clojuredocs.org/clojure.core/re-find

non capturing regex look behind

```
(re-seq #"(?:[^o])y" "toy")
nil
```

