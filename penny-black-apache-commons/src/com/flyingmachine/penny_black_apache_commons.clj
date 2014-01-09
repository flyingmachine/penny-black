(ns com.flyingmachine.penny-black-apache-commons
  (:require [com.flyingmachine.penny-black.core.send :refer (send-with-backend)]
            [com.flyingmachine.penny-black.core.config :refer (config)])
  (:import org.apache.commons.mail.HtmlEmail))

(defmethod send-with-backend :default [params]
  (do
    (let [email (HtmlEmail.)]
      (doto email
        (.setHostName (config :host))
        (.setSslSmtpPort (config :ssl-smtp-port))
        (.setSSL (config :use-ssl))
        (.addTo (:to params))
        (.setFrom (config params :from-address) (config params :from-name))
        (.setSubject (:subject params))
        (.setAuthentication (config params :authentication :username)
                            (config params :authentication :password)))
      (if-let [text (get-in params [:body :text])]
        (.setTextMsg email text))
      (if-let [html (get-in params [:body :html])]
        (.setHtmlMsg email html))
      (.send email))
    true))

