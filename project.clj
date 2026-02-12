(defproject br.eng.crisjr.static_site_generator "0.3.3"
  :description "Static Site Generator"
  :url "https://www.crisjr.eng.br/notes"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [org.clojure/data.json "2.5.1"]
                 [markdown-clj "1.12.4"]
                 [org.clojars.liberdade/strint "0.0.1"]]
  :main ^:skip-aot br.eng.crisjr.static-site-generator
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
