;; The MIT License (MIT)
;;
;; Copyright (c) 2015 Arjen Wiersma
;;
;; Permission is hereby granted, free of charge, to any person obtaining a copy
;; of this software and associated documentation files (the "Software"), to deal
;; in the Software without restriction, including without limitation the rights
;; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;; copies of the Software, and to permit persons to whom the Software is
;; furnished to do so, subject to the following conditions:
;;
;; The above copyright notice and this permission notice shall be included in all
;; copies or substantial portions of the Software.
;;
;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
;; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
;; SOFTWARE.

(ns threesixfive.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clj-template.html5 :as h]
            [clj-time.core :as t]
            [clj-time.format :as fmt]
            [clojure.data.json :as json]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [net.cgrand.enlive-html :as html]
            [selmer.parser :as s]))

(def custom-formatter (fmt/formatter "yyyy-MM-dd"))

(defn is-threesixfive? [caption]
  (if (= (re-matches #"^\d{1,3}/\d{3}.*" caption) nil)
    false
    true))

(defn get-threesixfive [caption]
  (first (map second (re-seq #"^(\d+)/(\d+).+" caption))))

(defn retrieve-image [url filename]
  (clojure.java.io/copy
   (:body (client/get url {:as :stream}))
   (java.io.File. filename)))

(defn caption->markdown [caption]
  (str/replace caption #"((:?\#\s|\#\w+))" "**\\\\$1**")
  )

(defn epoch->datetime [secondsSinceEpoch]
  (t/plus (t/epoch) (t/seconds secondsSinceEpoch)))

(defn create-post [config data]
  (println "Creating post " data)
  (let [postdir (-> config :output :posts)]
    (spit (str postdir "/" (:createdate data) "-" (:index data)  ".md")
     (s/render-file "post.selmer" data)))
  )

(defn proces-post [config post]
  (when-not (nil? (:caption post))
    (let [caption (-> post :caption :text)]
      (if  (is-threesixfive? caption)
        (let [image (-> post :images :standard_resolution :url)
              thumb (-> post :images :thumbnail :url)
              created (-> post :created_time)
              date (epoch->datetime (Long/parseLong created))
              datestr (fmt/unparse custom-formatter date)
              dir (-> config :output :images)
              index (get-threesixfive caption)]
          ;; Download images
          (retrieve-image image (str dir "/" index ".jpg"))
          (retrieve-image thumb (str dir "/" index "-thumb.jpg"))
          ;; create post
          (create-post config {:index (get-threesixfive caption)
                               :caption (caption->markdown caption)
                               :createdate datestr}))))))

(defn get-instagram-url [config]
  (str "https://api.instagram.com/v1/users/" (:userid config) "/media/recent/?client_id="
       (:client_id config)
       (if (:lastid config) (str "&min_id=" (:lastid config)))
       "&count=10")
  )

(defn get-last-id [config newid]
  (if (nil? (:lastid config))
    newid
    (if (nil? newid)
      (:lastid config)
      (let [last (:lastid config)
            lhs (Long/parseLong newid)
            rhs (if (number? last) last (Long/parseLong last))]
        (if (> lhs rhs)
          lhs
          rhs)))))

(defn get-instagram-posts [config url]
  (let [data (client/get url)
        result (walk/keywordize-keys (json/read-str (:body data)))]
    (if (and (not (empty? (:data result)))
             (not (empty? (first (:data result)))))
      (let [lastid (get-last-id config (-> (first (:data result)) :caption :id))
            newconfig (assoc config :lastid lastid)]
        (doall (map #(proces-post newconfig %) (:data result)))
        (if  (empty? (:pagination result))
          newconfig
          (recur newconfig (-> result :pagination :next_url))
          )
        )
      config)
    )
  )

(defn iterate-instragram [config]
  (get-instagram-posts config  (get-instagram-url config))
  )

(defn -main []
  (println "Starting to retrieve Instagram posts")
  (let [config (read-string (slurp "config.edn"))]
    (spit "config.edn" (iterate-instragram config)))
  (println "Done with Instagram")
  )
