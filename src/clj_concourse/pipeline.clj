(ns clj-concourse.pipeline
    (:use [clj-concourse.job :only [->job-list map->job]])
    (:require [clj-concourse.api :as api]))

(defrecord pipeline-list [team]
  api/endpoint
  (api/method [ps] :GET)
  (api/url [ps] (str (api/url (:team ps)) "/pipelines"))
  api/authed
  (api/->team [ps] (:team ps)))

(defrecord pipeline [team name]
  api/endpoint
  (api/method [p] :GET)
  (api/url [p] (str (api/url (:team p)) "/pipelines/" (:name p)))
  api/authed
  (api/->team [p] (:team p)))

(defn jobs
  [p]
  (api/result (->job-list p)))

(defn job
  [p name]
  (api/result (map->job :pipeline p :name name)))
