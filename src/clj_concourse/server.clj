(ns clj-concourse.server
  (:require [clj-concourse.api :as api]
            [clj-concourse.team :refer [->team-list]]))

(defrecord server [url]
  api/endpoint
  (api/method [o] :GET)
  (api/url [o] (str (:url o) "/api/v1")))

(defn teams
  [server]
  (api/result (->team-list server)))
