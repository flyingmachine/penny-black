(defproject com.flyingmachine/penny-black-core "0.1.0-SNAPSHOT"
  :description "email sending templates and core sending functions"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [stencil "0.3.2"]
                 [environ "0.4.0"]]

  :plugins [[lein-environ "0.4.0"]]

  :profiles {:dev {:dependencies [[midje "1.5.0"]]
                   :env {:com-flyingmachine-penny-black
                         {:template-path "email-templates"
                          :send-email false
                          :test-to "nonrecursive+test-to@gmail.com"
                          :test-from "nonrecursive+test-from@gmail.com"}}}
             :test {:env {:com-flyingmachine-penny-black
                          {:template-path "email-templates"
                          :send-email false
                          :test-to "nonrecursive+test-to@gmail.com"
                          :test-from "nonrecursive+test-from@gmail.com"}}}})