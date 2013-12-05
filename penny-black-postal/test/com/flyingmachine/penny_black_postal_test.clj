(ns com.flyingmachine.penny-black-postal-test
  (:require [com.flyingmachine.penny-black.core.config :refer (config)]
            [com.flyingmachine.penny-black.core.send :as send]
            [com.flyingmachine.penny-black.core.templates :as templates]
            com.flyingmachine.penny-black-postal)
  (:use midje.sweet))

(def template-vars
  {:var1 "a"
   :var2 "b"
   :htmlvar1 "<p>a</p>"
   :htmlvar2 "<p>b</p>"})

(def mail-options
  {:from (config :test-from)
   :to (config :test-to)
   :subject "penny black postal test"
   :body (templates/body "test" template-vars)})

(fact "if for-reals? is true, return success and send email"
  (send/send-email*
   true
   mail-options)
  => {:code 0, :error :SUCCESS, :message "messages sent"})

(fact "should create a function which returns nil and actually sends email"
  (send/defsenders
    {:args [users]
     :user-doseq [user users]}

    {:from (config :test-from)
     :body-data {:var1 "a"
                 :var2 "b"
                 :htmlvar1 "<p>a</p>"
                 :htmlvar2 "<p>b</p>"}}

    (send-test
     []
     :to (:email user)
     :subject "Postal defsender test"))
  (send-test [{:email (config :test-to)}])
  => nil)