(ns com.flyingmachine.penny-black.templates-test
  (:require [com.flyingmachine.penny-black.send :as send]
            [com.flyingmachine.penny-black.templates :as templates]
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