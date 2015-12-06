(ns threesixfive.core-test
  (:require [clojure.test :refer :all]
            [threesixfive.core :refer :all]))

(deftest is-threesixfive
  (testing "is-threesixfive?"
    (is (= true (is-threesixfive? "54/365 de sag na #sinterklaas is voor ons de traditionele maak-je-huis-mooi-voorkerst-dag... Dit jaar met buitenverlichting op de veranda, een #trein onder de #kerstboom. #heerlijk #365 #christmas")))
    (is (= true (is-threesixfive? "1/365")))
    (is (= true (is-threesixfive? "52/365")))
    (is (= false (is-threesixfive? "test")))
    (is (= false (is-threesixfive? "/121")))
    (is (= false (is-threesixfive? "1/1")))
    (is (= false (is-threesixfive? "1234/121")))
    ))

(deftest get-threesixfive-test
  (testing "retrieve the index"
    (is (= "50" (get-threesixfive "50/365 test")))
    (is (= nil (get-threesixfive "51 test")))
    ))

(deftest caption-parser-test
  (testing "escaping of markdown"
    (is (= "**\\#test** geen bold **\\# **geen bold" (caption->markdown "#test geen bold # geen bold")))))
