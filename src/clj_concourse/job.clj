(ns clj-concourse.job
  (:require [clj-concourse.api :as api]))

(defrecord job-list [pipeline]
  api/endpoint
  (api/method [js] :GET)
  (api/url [js] (str (api/url (:pipeline js)) "/jobs"))
  api/authed
  (api/->team [js] (api/->team (:pipeline js))))

;; a map of job_name -> { inputs: [names], outputs: [name] }
(def ^:private jobs->resource-map
  (memoize (fn
  [jobs]
  (into {} (map
  (fn [job]
    [(:name job)
       { :outputs
         (map :resource (:outputs job)),
         :inputs
         (map :resource (:inputs job))}
     ])
  jobs)))))

(defn is-terminal-job?
      "Returns true if no outputs of this job is an input to any other job in the collection"
      [jobs job-name]
      (let [ resource-map (jobs->resource-map jobs)
             all-inputs (into #{} (flatten (map :inputs (vals resource-map))))
             job-outputs (:outputs (get resource-map job-name)) ]
        (not-any?
          #(contains? all-inputs %)
          job-outputs)))
