(ns ws-load-test.core
  (:require [org.httpkit.server :as server]
            [org.httpkit.client :as client]
            [taoensso.carmine   :as car])
  (:gen-class))
(def server1-conn {:spec {:host "localhost" :port 32769}})
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

(defn -main
  [& args]
  (println "start server at 4001")
  (start-server 4001))

(defn set-zero [n v]
  (car/lua "local i = 1
              local res
              local n = tonumber(_:my-key)
              local v = tonumber(_:my-val)
              while (i < n) do
               redis.call('set', i, v)
               i = i+1
              end
              return res"
           {:my-key n}
           {:my-val v}))

(defonce server (atom nil))
(defonce running-port (atom nil))

(defn app [reg]
  (let [param (subs (:uri reg) 1)
        _     (wcar* (set-zero param 0))]
    (prn param)
    {:header {"Content-Type""text"}
     :body param}))

(defn start-server [port]
  (println "start server at " port)
  (reset! running-port port)
  (->> {:port port}
       (server/run-server app)
       (reset! server)))
(defn stop-server []
  (when-not (nil? @server)
    (@server)
    (reset! server nil)))

(defn restart-server []
  (stop-server)
  (start-server 4000))

(comment
  (-main)
  (restart-server)
  (def n 20)
  (wcar* (car/ping))
  (defn set-zero [n v]
    (car/lua "local i = 1
              local res
              local n = tonumber(_:my-key)
              local v = tonumber(_:my-val)
              while (i < n) do
               redis.call('set', i, v)
               i = i+1
              end
              return res"
             {:my-key n}
             {:my-val v}))
  (wcar* (set-zero "10" "7"))
  (:status @(client/get (str "http://localhost:" @running-port "/" n)))
  #_())