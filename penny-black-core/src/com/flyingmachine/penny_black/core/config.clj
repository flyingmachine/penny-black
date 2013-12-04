(ns com.flyingmachine.penny-black.core.config
  (:require [com.flyingmachine.config :refer :all]
            [environ.core :refer [env]]))

(defconfig config env :com-flyingmachine-penny-black)