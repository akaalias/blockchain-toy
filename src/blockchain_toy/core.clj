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

(with-test
  (defn make-nonce-for-zeros-finder [z-count max]
    (fn [num data nonce]
      (cond (> nonce max) nil
            (= (clojure.string/join (repeat z-count "0")) (subs (generate-hash num data nonce) 0 z-count)) nonce
            :else (recur num data (inc nonce)))))

  (is (= ((make-nonce-for-zeros-finder 1 100) nil nil 100) nil))
  (is (= ((make-nonce-for-zeros-finder 1 100) nil nil 0) 39))
  (is (= ((make-nonce-for-zeros-finder 1 100) 1 nil 0) 25))
  (is (= ((make-nonce-for-zeros-finder 1 100) 1 1 0) 11))
  (is (= ((make-nonce-for-zeros-finder 1 100) "foo" "bar" 0) 20))

  (is (= ((make-nonce-for-zeros-finder 2 1000) nil nil 0) 286))
  
  (is (= ((make-nonce-for-zeros-finder 4 100000) nil nil 100000) nil))
  (is (= ((make-nonce-for-zeros-finder 4 100000) nil nil 100001) nil))
  (is (= ((make-nonce-for-zeros-finder 4 100000) nil nil 0) 88484))
  (is (= ((make-nonce-for-zeros-finder 4 100000) 1 1 0) 64840))
  (is (= ((make-nonce-for-zeros-finder 4 100000) "foo" "bar" 0) 42515)))


(generate-hash "foo" "bar" 20)
