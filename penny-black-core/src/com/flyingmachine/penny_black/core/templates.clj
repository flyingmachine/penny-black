(ns com.flyingmachine.penny-black.core.templates
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [clojure.contrib.core :refer (-?>)]
            [environ.core :refer :all]))

(extend-protocol stencil.ast/ASTNode
  nil
  (render [this ^StringBuilder sb context-stack]
    nil))

(defn template-path
  [template-name extension]
  (str (get-in env [:com-flyingmachine-penny-black :template-path])
       "/" template-name "." extension))

(defn apply-template
  [name extension data]
  (let [path (template-path name extension)]
    (if (stencil.loader/load path)
      (stencil/render-file path data))))

(defn body
  [template-name data]
  {:text (apply-template template-name "txt" data)
   :html (apply-template template-name "html" data)})