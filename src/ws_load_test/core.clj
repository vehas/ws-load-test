(ns ws-load-test.core
  (:require [org.httpkit.server :as server]
            [org.httpkit.client :as client]
            [taoensso.carmine   :as car])
  (:gen-class))
(def server1-conn {:spec {:host "localhost" :port 6379}})
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

;(defn -main
;  [& args]
;  (println "start server at 4001")
;  (start-server 4001))
(defn x [n]
  (inc n))

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
(defonce a 12)
(defonce reg-atom (atom nil))
(defn ws-1 [n]
  (:status @(client/get (str "http://localhost:" @running-port "/2/" n))))
(ws-1 "10")

(defn ws-2 [n]
  (wcar* car/incr))
(ws-2 10)
(defn app [reg]
  (println "req :" reg)
  (reset! reg-atom reg)
  (let [ws-no (-> (:uri reg)
                  (subs 1)
                  (subs 0 1))
        _     (println "ws no :" ws-no)
        _     (println "ws no int  : " (type (Integer/parseInt ws-no)))
        no    (-> (:uri reg)
                  (subs 3))
        param "11"
        _   (println "no : " no)]
        ;_     (wcar* (set-zero ws-no 0))]
    (case (Integer/parseInt ws-no)
      0 (wcar* (set-zero ws-no 0))
      1 (ws-1 no)
      2 (do
          (println "get :" no)
          (ws-2 no))
      ;3 (ws-3)
      :no-mactch)
    ;(prn param)
    {:header {"Content-Type""text"}
     :body param}))
(comment
  (app {:uri "/1/10"})
  (app {:uri "/2/12"})
  (app {:uri "/3/13"})
  #_ ())

(defn start-server [port]
  (println "start server at " port)
  (reset! running-port port)
  (->> {:port port}
       (server/run-server app)
       (reset! server)))


(+ 1 (+ 1 2))
(-> 1
    (+ 2)
    (+ 1))
(filter odd? (map inc [1 2 3 4 5 6]))
(-> [1 2 3 4]
    (nth 2))

(nth [1 2 3 4] 2)

(->> (range 10)
    (map inc)
    (filter odd?)
    (drop  3))

(defn stop-server []
  (when-not (nil? @server)
    (@server)
    (reset! server nil)))

(defn restart-server []
  (stop-server)
  (start-server 4000))


(defn -main
  [& args]
  (println "start server at 4001")
  (start-server 4001))
;; ws 0 ( /0/:n ) -> reset  all key 0 to n in redis to 0
;; ws 1 (  /1/:n ) -> (open port 4000 route to /2/) send random number to  ws 2
;; ws 2 ( /2/:n )-> (open port 4000 route to /3/) add one to value of number from ws 1 to redis and send that number to ws 3
;; ws 3 ( /3/:n ) -> (open port 4000) read value of received number to  redis and send back
;(println "start server at 4001")

(comment
  (-main)
  (wcar* (car/ping))
  (restart-server)
  (stop-server)
  (def n 20)
  (println "aaa")
  (wcar* (car/ping))
  (defn set-zero [n v]
    (car/lua "local i = 1
              local res
              local n = tonumber(_:my-key)
              local v = tonumber(_:my-val)
              while (i <= n) do
               redis.call('set', i, v)
               i = i+1
              end
              return res"
             {:my-key n}
             {:my-val v}))
  (wcar* (set-zero "10" "7"))
  (:status @(client/get (str "http://localhost:" @running-port "/" n)))
  #_())
