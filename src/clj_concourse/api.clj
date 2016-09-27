(ns clj-concourse.api
  (:require [clj-http.client :as http]
            [cheshire.core :as json])
  (:use [slingshot.slingshot :only [try+ throw+]]
        [clojure.walk :only [keywordize-keys]]))

(defprotocol endpoint
  (method [o] "Which HTTP verb")
  (url    [o] "The fully-qualified url of the endpoint"))

(defprotocol authed
  (->team [o] "Fetch team corresponding to object (for auth)"))
(defprotocol unauthed)

(declare authed-get)

(defmulti result (fn [api-object] [(if (satisfies? authed api-object) authed unauthed) (method api-object)]))
(defmethod result [ authed :GET ]
  [o]
  (authed-get (->team o) (url o)))
(defmethod result [ unauthed :GET]
  [o]
  (keywordize-keys
    (json/decode (:body (http/get (url o))))))


(def ^:private with-auth-token
  (memoize (fn
    [team]
    (if (contains? team :token)
      team
      ;; else
      (let [ url (str (url team) "/auth/token")
             username (:basic_auth_username team)
             password (:basic_auth_password team)
             response (http/get url {:basic-auth [username password]})]
      (assoc team :token
              (get (json/decode (:body response)) "value")))))))

(defn- authed-get
  ([team url] (authed-get team url false))
  ([team url is-retry?]
    (let [ token (:token (with-auth-token team))
           cookie (str "ATC-Authorization=Bearer " token)]
    (try+
      (def response (http/get url {:headers {:cookie cookie}}))
      (catch [:status 401] {}
        (if-not is-retry?
          (let [ tt (dissoc :token team)
                 team-for-reauth (assoc tt :auth-attempt (+ (:auth-attempt tt) 1))]
          (authed-get team-for-reauth url true))
          (throw+))))
    (keywordize-keys
      (json/decode (:body response))))))
