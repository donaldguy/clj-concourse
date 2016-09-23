# clj-concourse

A [Concourse](https://concourse.ci) API Library in Clojure

## Usage

(ns main
  (:use clj-concourse :as concourse))

(defn -main
  []
   (let [s (concourse/->server "https://ci.concourse.ci")
         t (concourse/map->team { :server s,
                                  :name "main",
                                  :basic_auth_username "bot"
                                  :basic_auth_password "changeme")
         mj (jobs (map->pipeline {:team t , :name "main"}))]
         (pprint (map :name (filter #(is-terminal-job? mj (:name %)) mj)))))
