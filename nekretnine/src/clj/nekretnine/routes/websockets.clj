(ns nekretnine.routes.websockets
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]
            [clojure.edn :as edn]
            [nekretnine.adrese :as adr]))


(defonce channels (atom #{}))

(defn connect! [channel]
  (log/info "Channel opened")
  (swap! channels conj channel))

(defn disconnect! [channel status]
  (log/info "Channel closed: " status)
  (swap! channels disj channel))


(defn handle-adresa! [channel ws-adresa]
  (let [adresa (edn/read-string ws-adresa)
        response (try
                   (adr/save-adresa! adresa)
                   (assoc adresa :timestamp (java.util.Date.))
                   (catch Exception e
                     (let [{id :nekretnine/error-id
                            errors :errors} (ex-data e)]
                       (case id
                         :validation
                         {:errors errors}
;;else
                         {:errors
                          {:server-error ["Neuspesno cuvanje adrese"]}}))))]
    (if (:errors response)
      (http-kit/send! channel (pr-str response))
      (doseq [channel @channels]
        (http-kit/send! channel (pr-str response))))))

(defn handler [request]
  (http-kit/with-channel request channel
    (connect! channel)
    (http-kit/on-close channel (partial disconnect! channel))
    (http-kit/on-receive channel (partial handle-adresa! channel))))

(defn websocket-routes []
  ["/ws"
   {:get handler}])