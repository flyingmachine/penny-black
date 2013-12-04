(ns com.flyingmachine.penny-black-apache-commons-test
  (:require [com.flyingmachine.penny-black.core.config :refer (config)]
            [com.flyingmachine.penny-black.core.send :as send]
            [com.flyingmachine.penny-black.core.templates :as templates]
            com.flyingmachine.penny-black-apache-commons)
  (:use midje.sweet))

(def template-vars
  {:var1 "a"
   :var2 "b"
   :htmlvar1 "<p>a</p>"
   :htmlvar2 "<p>b</p>"})

(def mail-options
  {:from (config :test-from)
   :to (config :test-to)
   :subject "penny black test"
   :body (templates/body "test" template-vars)})

(fact "if for-reals? is true, return true and send email"
  (send/send-email*
   true
   mail-options)
  => true)