(ns com.flyingmachine.penny-black.core.send-test
  (:require [com.flyingmachine.penny-black.core.send :as send]
            [com.flyingmachine.penny-black.core.templates :as templates]
            [com.flyingmachine.penny-black.core.config :refer (config)]
            [environ.core :refer :all])
  (:use midje.sweet))

(def template-vars
  {:var1 "a"
   :var2 "b"
   :htmlvar1 "<p>a</p>"
   :htmlvar2 "<p>b</p>"})

(def mail-options
  {:from (get-in env [:com-flyingmachine-penny-black :test-from])
   :to (get-in env [:com-flyingmachine-penny-black :test-to])
   :subject "penny black test"
   :body (templates/body "test" template-vars)})

(fact "if deliver? is false, return params"
  (send/send-email*
   false
   mail-options)
  => mail-options)


(fact "should create a function which returns nil"
  (send/defsenders
    {:args [users]
     :user-doseq [user users]}

    {:from (config :test-from)
     :to (config :test-to)}

    (send-test
     []
     :subject "Postal defsender test"))
  (send-test [{}])
  => nil)


