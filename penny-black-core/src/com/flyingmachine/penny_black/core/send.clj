(ns com.flyingmachine.penny-black.core.send
  (:require [com.flyingmachine.penny-black.core.templates :refer [body]]
            [com.flyingmachine.penny-black.core.config :refer [config]]
            [clojure.string :as s]))

(defmulti send-with-backend :x)

(defn send-email*
  [for-reals? params]
  (if for-reals?
    (send-with-backend params)
    params))

(defn send-email
  [params]
  (send-email* (config :send-email) params))

(defn to-uses-user?
  [user-doseq to]
  (or (and (coll? to) (some (set user-doseq) to))
      (= (first user-doseq) to)))

(defn to
  [{:keys [to] :as sender-params}
   {:keys [user-doseq] :as varnames}]
  (if (to-uses-user? user-doseq to)
    (merge sender-params {:to (list 'for user-doseq to)})
    sender-params))

(defn final-sender-params
  [defaults addl template-name]
  (let [final (merge defaults addl)
        body-data (merge (:body-data defaults) (:body-data addl))]
    (-> final
        (merge {:body (list body template-name body-data)})
        (dissoc :body-data))))

(defn defsender
  [varnames sender-param-defaults sender]
  (let [{:keys [args user-doseq]} varnames
        [sender-name addl-args & sender-params] sender
        template-name (s/replace sender-name #"^send-" "")
        sender-params (final-sender-params sender-param-defaults
                                           (apply hash-map sender-params)
                                           template-name)
        args (into args addl-args)]
    
    `(defn ~sender-name
       ~args
       (if (config :merge-to)
         (let [~(first user-doseq) nil]
           (send-email ~(to sender-params varnames)))
         (doseq ~user-doseq
           (send-email ~sender-params))))))

(defmacro defsenders
  [varnames sender-param-defaults & senders]
  `(do ~@(map #(defsender varnames sender-param-defaults %) senders)))
