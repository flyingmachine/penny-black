(ns com.flyingmachine.penny-black.core.templates-test
  (:require [com.flyingmachine.penny-black.core.templates :as templates])
  (:use midje.sweet))

(def template-vars
  {:var1 "a"
   :var2 "b"
   :htmlvar1 "<p>a</p>"
   :htmlvar2 "<p>b</p>"})

(fact "body returns applied template for text and html"
  (templates/body "test" template-vars)
  => (contains {:text "Test a b\n"}
               {:html "<p>Test <p>a</p> &lt;p&gt;b&lt;/p&gt;</p>\n"}
               :in-any-order))
