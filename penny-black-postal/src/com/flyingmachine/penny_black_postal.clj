(ns com.flyingmachine.penny-black-postal
  (:require [com.flyingmachine.penny-black.core.send :refer (send-with-backend)]
            [com.flyingmachine.penny-black.core.config :refer (config)]
            [postal.core :as postal]))

(defn postal-metadata
  [params]
  {:host (config :host)
   :ssl (config :use-ssl)
   :port (and (config :ssl-smtp-port) (Integer. (config :ssl-smtp-port)))
   :user (config params :authentication :username)
   :pass (config params :authentication :password)})

(defn html-body
  [body]
  [:alternative
   {:type "text/plain"
    :content (:text body)}
   {:type "text/html"
    :content (:html body)}])

(defn body
  [params]
  (if (:html params)
    (html-body params)
    (:text params)))

(defn from
  [params]
  (let [from-address (config params :from-address)
        from-name (config params :from-name)]
    (if from-name
      (str "\"" from-name "\" <" from-address ">")
      from-address)))

(defn postal-params
  [params]
  (merge params
         {:from (from params)
          :body (body (:body params))}))

(defmethod send-with-backend :default [params]
  [params]
  (postal/send-message (postal-metadata params) (postal-params params)))

