# blockchain-toy

Playing around with how hard it is to implement a toy (read: unoptimized!) algorithm to mine for nonces that, when added to the hashing input, produces n zeros at the beginning of the hash:

I came up with `make-nonce-for-zeros-finder`, a function which takes the numbers of zeros you want to have at the beginning and a maximum search-depth and returns a function that will calculate the nonce for your inputs.

## How to use

Let's say we have "foo" and "bar" as our input and would like to hash with another, yet unknown value, so that the hash that combines the three returns a hash with 1 zero at the beginning. 

To calculate that unknown value, the nonce, for 1 zero padding at the front, run

```clojure
((make-nonce-for-zeros-finder 1 100) "foo" "bar" 0)
;; => The nonce value is 20
```

(The hash for `"foo" x "bar" x 20` is "0fdc57809f5917eba08907d2805e43ce83f4c933a090b4a2b2549923a35e43d7")

It takes less than a millisecond to compute:

```clojure
(time ((make-nonce-for-zeros-finder 1 100) "foo" "bar" 0))
"Elapsed time: 0.439503 msecs"
```

Now, let's say you want to go up a notch and find the nonce that produces a hash with 2 zeros at the beginning. We have to increase how far we are willing to look, by increasing the max from 100 to 1000

```clojure
((make-nonce-for-zeros-finder 2 1000) "foo" "bar" 0)
;; => The nonce value is 102
```

The hash for `"foo" x "bar" x 102` is "006668bba91b7e2d5b5357b56600784edb77a72ecf86dc09d515853a841485f6". (Note the two zeros at the beginning.)

This will take slightly longer, 8 milliseconds:

```clojure
(time ((make-nonce-for-zeros-finder 2 1000) "foo" "bar" 0))
"Elapsed time: 8.936003 msecs"
```

You can, if you want to, increase how many zeros you want in the beginning but you'll have to adjust the max search depth accordingly. The more zeros you want and, I believe the longer the input is, the longer it will take to calcualate the nonce:

Finding a hash with 6 zeros padded, takes about a minute now

```clojure
(time ((make-nonce-for-zeros-finder 6 5000000) "foo" "bar" 0))
"Elapsed time: 64540.793452 msecs"
```

The actual code is short and reads reasonably well. The only messy part is that I have to manually create the matching string pattern by hand and, I guess, there is no way around getting the substring of the generated hash much easier...

```clojure
(defn make-nonce-for-zeros-finder [z-count max]
  (fn [num data nonce]
    (cond (> nonce max) nil
          (= (clojure.string/join (repeat z-count "0")) (subs (generate-hash num data nonce) 0 z-count)) nonce
          :else (recur num data (inc nonce)))))
```

## How to test

I've been a huge fan of the `with-test` macro. Because of that, you'll find all test-coverage in core.clj instead of a separate "_test.clj" file.

Run `lein test`

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
