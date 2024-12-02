(defproject finmngr "0.1.0-SNAPSHOT"
  :description "a simple finanace manager application"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
               [seesaw "1.5.0"]
               [cheshire "5.11.0"]]  ; Optional for JSON handling  
  :main finmngr.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
