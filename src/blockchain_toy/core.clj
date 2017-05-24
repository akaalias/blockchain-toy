(ns blockchain-toy.core
  (:require [clojure.test :refer :all]
            [digest :refer :all]))
(with-test
  (defn generate-hash [num data nonce]
    (sha-256 (str num nonce data)))

  (is (= (generate-hash nil nil nil) "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"))
  (is (= (generate-hash 1 nil nil) "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b"))
  (is (= (generate-hash 1 1 nil) "4fc82b26aecb47d2868c4efbe3581732a3e7cbcc6c2efb32062c08170a05eeb8"))
  (is (= (generate-hash 1 1 1) "f6e0a1e2ac41945a9aa7ff8a8aaa0cebc12a3bcc981a929ad5cf810a090e11ae"))
  (is (= (generate-hash 2 1 1) "093434a3ee9e0a010bb2c2aae06c2614dd24894062a1caf26718a01e175569b8")))

(defn generate-zeros-string [z-count]
  (clojure.string/join (repeat z-count "0")))

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

(def find-nonce-for-seven-zeros-padded-hash
  (make-nonce-for-zeros-finder 7))

(def find-nonce-for-eight-zeros-padded-hash
  (make-nonce-for-zeros-finder 8))

;; and so on...

;; (time (find-nonce-for-eight-zeros-padded-hash "foo" "bar"))

