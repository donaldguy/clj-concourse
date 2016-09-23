(ns clj-concourse.team
    (:require [clj-concourse.api :as api])
    (:use [clj-concourse.pipeline :only [->pipeline-list]]))

(defrecord team-list [server]
  api/endpoint
  (api/method [ts] :GET)
  (api/url [ts] (str (api/url (.server ts)) "/teams")))

(defrecord team [server name basic_auth_username basic_auth_password]
  api/endpoint
  (api/method [t] :GET)
  (api/url [t] (str (api/url (:server t)) "/teams/" (:name t)))
  api/authed
  (->team [t] t))

(defn pipelines
  [t]
  (api/result (->pipeline-list t)))
