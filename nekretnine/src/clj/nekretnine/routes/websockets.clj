(ns nekretnine.routes.websockets
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :as http-kit]
            [clojure.edn :as edn]
            [nekretnine.adrese :as adr]
            [nekretnine.middleware :as middleware]
            [mount.core :refer [defstate]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]))


(defstate socket
  :start (sente/make-channel-socket!
          (get-sch-adapter)
          {:user-id-fn (fn [ring-req]
                         (get-in ring-req [:params :client-id]))}))

(defn send! [uid adrese]
  (println "Slanje adrese: " adrese)
  ((:send-fn socket) uid adrese))


(defonce channels (atom #{}))

(defn connect! [channel]
  (log/info "Channel opened")
  (swap! channels conj channel))

(defn disconnect! [channel status]
  (log/info "Channel closed: " status)
  (swap! channels disj channel))

(defmulti handle-adresa (fn [{:keys [id]}]
                          id))

(defmethod handle-adresa :default
  [{:keys [id]}]
  (log/debug "Received unrecognized websocket event type: " id)
  {:error (str "Unrecognized websocket event type: " (pr-str id))
   :id id})


(defmethod handle-adresa :adrese/create!
  [{:keys [?data uid] :as adresa}]
  (let [response (try
                   (adr/save-adresa! ?data)
                   (assoc ?data :timestamp (java.util.Date.))
                   (catch Exception e
                     (let [{id :nekretnine/error-id
                            errors :errors} (ex-data e)]
                       (case id
                         :validation
                         {:errors errors}
;;else
                         {:errors
                          {:server-error ["Neuspesno cuvanje "]}}))))]
    (if (:errors response)
      (do
        (log/debug "Neuspesno cuvanje  " ?data)
        response)
      (do
        (doseq [uid (:any @(:connected-uids socket))]
          (send! uid [:adrese/add response]))
        {:success true}))))



(defn receive-adresa! [{:keys [id ?reply-fn]
                        :as adresa}]
  (log/debug "Dobijena adresa: " id)
  (let [reply-fn (or ?reply-fn (fn [_]))]
    (when-some [response (handle-adresa adresa)]
      (reply-fn response))))







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

(defstate channel-router
  :start (sente/start-chsk-router!
          (:ch-recv socket)
          #'receive-adresa!)
  :stop (when-let [stop-fn channel-router]
          (stop-fn)))

(defn websocket-routes []
  ["/ws"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]
    :get (:ajax-get-or-ws-handshake-fn socket)
    :post (:ajax-post-fn socket)}])


