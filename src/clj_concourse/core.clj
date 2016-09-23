(ns clj-concourse.core
  (:use clj-concourse.team
        clj-concourse.pipeline
        clj-concourse.job
        clojure.pprint)
  (:require [clj-concourse.api :as api]))

(defrecord server [url]
  api/endpoint
  (api/method [o] :GET)
  (api/url [o] (str (:url o) "/api/v1")))

(defn teams
  [server]
  (api/result (->team-list server)))
