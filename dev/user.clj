(ns user
  (:require [reloaded.repl :refer [system init start stop go reset reset-all]]
            [gentest-playground.system :as system]))

(reloaded.repl/set-init! system/dev-system)
