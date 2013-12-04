(ns com.flyingmachine.penny-black-apache-commons-test
  (:use clojure.test
        com.flyingmachine.penny-black-apache-commons))

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

(fact "if for-reals? is true, return true and send email"
  (send/send-email*
   true
   mail-options)
  => mail-options)