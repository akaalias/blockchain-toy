[![Build Status](https://travis-ci.org/akaalias/blockchain-toy.svg?branch=master)](https://travis-ci.org/akaalias/blockchain-toy)

# Calculating Nonces That Result In Zero-Padded SHA256 Hashes

Yesterday I watched [Anders Brownworth's awesome Blockchain demo](https://github.com/anders94/blockchain-demo) and got inspired to follow along in Clojure.

I _had_ to play around with implementing a toy (read: unoptimized!) algorithm to "mine" for nonces that, when added to the hashing input, produces n zeros at the beginning of the hash:

I came up with `make-nonce-for-zeros-finder`, a function which takes the numbers of zeros you want to have at the beginning and a maximum search-depth and returns a function that will calculate the right nonce for your inputs.

What I personally enjoyed about this little exercise was to express an iterative process in a simple recursive function. And to make a function-maker once I wanted to play around with different types of hashes/nonces I wanted to find.

```clojure
(with-test
  (defn make-nonce-for-zeros-finder [z-count]
    (fn
      ([num data]
       ((make-nonce-for-zeros-finder z-count) num data 0))
      ([num data nonce]
       (cond (= (generate-zeros-string z-count) (subs (generate-hash num data nonce) 0 z-count)) nonce
             :else (recur num data (inc nonce))))))

  (is (= ((make-nonce-for-zeros-finder 1) nil nil) 39))
  (is (= ((make-nonce-for-zeros-finder 1) 1 nil) 25))
  (is (= ((make-nonce-for-zeros-finder 1) 1 1) 11))
  (is (= ((make-nonce-for-zeros-finder 1) "foo" "bar" 0) 20))
  (is (= ((make-nonce-for-zeros-finder 2) nil nil) 286))
  (is (= ((make-nonce-for-zeros-finder 4) nil nil) 88484))
  (is (= ((make-nonce-for-zeros-finder 4) 1 1) 64840))
  (is (= ((make-nonce-for-zeros-finder 4) "foo" "bar") 42515)))
```

With it, I can quickly create finders for different lengths of zero paddings:

```clojure
(def find-nonce-for-one-zero-padded-hash
  (make-nonce-for-zeros-finder 1))

(def find-nonce-for-two-zeros-padded-hash
  (make-nonce-for-zeros-finder 2))

(def find-nonce-for-three-zeros-padded-hash
  (make-nonce-for-zeros-finder 3))

(def find-nonce-for-four-zeros-padded-hash
  (make-nonce-for-zeros-finder 4))

(def find-nonce-for-five-zeros-padded-hash
  (make-nonce-for-zeros-finder 5))

(def find-nonce-for-six-zeros-padded-hash
  (make-nonce-for-zeros-finder 6))

;; and so on...

```

With these guys set up, we can now calculate the nonce for hashes with 1, 2, 3, 4 and 5 zeros padded. Let's see how that looks:

```clojure
(find-nonce-for-one-zero-padded-hash "foo" "bar")
;; => 20
;; takes less than a millisecond

;; we can verify that the hash has one zero at the beginning:
(generate-hash "foo" "bar" 20)
;; => "0fdc57809f5917eba08907d2805e43ce83f4c933a090b4a2b2549923a35e43d7"

(find-nonce-for-two-zeros-padded-hash "foo" "bar")
;; => 102
;; takes less than a millisecond

;; we can verify that the hash has two zeros at the beginning:
(generate-hash "foo" "bar" 102)
;; => "006668bba91b7e2d5b5357b56600784edb77a72ecf86dc09d515853a841485f6"

(find-nonce-for-three-zeros-padded-hash "foo" "bar")
;; => 4663
;; takes less than a millisecond

;; we can verify that the hash has three zeros at the beginning:
(generate-hash "foo" "bar" 4663)
;; => "0005d9cd6c13fe6bf56e169fd2a7008003fc3a4c6539f8a1cf7d82975d00210e"

```

And that's really all there is to it. Once you cross 6 padded zeros, you'll notice an increase in time it takes to compute.


## How to test

I've been a huge fan of the `with-test` macro. Because of that, you'll find all test-coverage in core.clj instead of a separate "_test.clj" file.

To run all tests, use `lein test`

## License

Copyright © 2017 Alexis Rondeau

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
