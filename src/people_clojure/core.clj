(ns people-clojure.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))

(defn read-people []                                        ;moved all the code below from the main function into the read-people function
  (let [people (slurp "people.csv")
        people (str/split-lines people)
        people (map (fn [line]
                      (str/split line #","))
                    people)
        header (first people)
        people (rest people)
        people (map (fn [line]
                      (interleave header line))
                    people)
        people (map (fn [line]
                      (apply hash-map line))
                    people)
        people (walk/keywordize-keys people)
        people (filter (fn [line]
                         (= "Brazil" (:country line)))
                       people)]
    #_(spit "filtered_people.edn"
          (pr-str people))
    people))

(defn people-html []
  (let [people (read-people)]
    (map (fn [line]
           [:p                          ;this could also be ":br"
            (str (:first_name line)     ;str used to concatenate two things together
                 " "                    ;space is so it shows firstname SPACE lastname on the web server
                 (:last_name line))])
         people)))

(defn handler [request]
  {:status  200
   :headers {"Content-type" "text/html"}
   :body    (h/html [:html
                     [:body
                      [:a {:href "http://www.theironyard.com"}
                       "The Iron Yard"]
                      [:br]
                      (people-html)]])})

(defn -main [& args]
  (j/run-jetty #'handler {:port 3000 :join? false}))                       ;port is the localhost port you run the site through
