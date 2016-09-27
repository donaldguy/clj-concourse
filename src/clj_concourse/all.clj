(ns clj-concourse.all
  (:require [clj-concourse.server]
            [clj-concourse.team]
            [clj-concourse.pipeline]
            [clj-concourse.job]))

;; I "optionally" seperated the api objects into their own namespaces, but
;; most clients are gonna just wanna use it all, so I flatten to
;; this namespace so it can all be imported at once. per
;; http://stackoverflow.com/questions/4732134/can-i-refer-another-namespace-and-expose-its-functions-as-public-for-the-current
(defmacro pullall [ns]
  `(do ~@(for [i (map first (ns-publics ns))]
           `(def ~i ~(symbol (str ns "/" i))))))

(pullall clj-concourse.server)
(pullall clj-concourse.team)
(pullall clj-concourse.pipeline)
(pullall clj-concourse.job)
