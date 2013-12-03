(ns com.flyingmachine.penny-black-apache-commons
  (:require com.flyingmachine.penny-black.core.send)
  (:import org.apache.commons.mail.HtmlEmail))


(defmethod send-with-backend :default [params]
  (do
    (doto (HtmlEmail.)
      (.setHostName (config/setting core/config-key :hostname))
      (.setSslSmtpPort (config/setting core/config-key :ssl-smtp-port))
      (.setSSL (config/setting core/config-key :use-ssl))
      (.addTo (:to params))
      (.setFrom (params-or-config :from-address) (params-or-config :from-name))
      (.setSubject (:subject params))
      (.setTextMsg (get-in params [:body :text]))
      (.setHtmlMsg (get-in params [:body :html]))
      (.setAuthentication (params-or-config :authentication :username)
                          (params-or-config :authentication :password))
      (.send))
    true))

