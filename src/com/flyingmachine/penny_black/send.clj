(ns com.flyingmachine.penny-black.send
  (:require [com.flyingmachine.email.sending.content :refer [body]]
            [environ.core :refer :all]))

(defn send-email*
  [for-reals? params]
  (if for-reals?
    (cond
     (= (config/setting core/config-key :backend) :postal)
     (send-with-postal params)
     
     (= (config/setting core/config-key :backend) :apache-commons)
     (send-with-apache-commons params))
    params))

(defn send-email
  [params]
  (send-email* (config/setting :com.flyingmachine.email :send-email) params))

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
       (doseq ~user-doseq
         (send-email ~sender-params)))))

(defmacro defsenders
  [varnames sender-param-defaults & senders]
  `(do ~@(map #(defsender varnames sender-param-defaults %) senders)))