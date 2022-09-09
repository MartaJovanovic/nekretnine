(ns nekretnine.websockets
  (:require-macros [mount.core :refer [defstate]])
  (:require [cljs.reader :as edn]
            [re-frame.core :as rf]
            [taoensso.sente :as sente]
            mount.core))


(defstate socket
  :start (sente/make-channel-socket!
          "/ws"
          (.-value (.getElementById js/document "token"))
          {:type :auto
           :wrap-recv-evs? false}))


(defonce channel (atom nil))

(defn connect! [url receive-handler]
  (if-let [chan (js/WebSocket. url)]
    (do
      (.log js/console "Connected!")
      (set! (.-onadresa chan) #(->> %
                                    .-data
                                    edn/read-string
                                    receive-handler))
      (reset! channel chan))
    (throw (ex-info "Websocket Connection Failed!"
                    {:url url}))))


(defn send! [& args]
  (if-let [send-fn (:send-fn @socket)]
    (apply send-fn args)
    (throw (ex-info "Kanal zatvoren. Adresa nije poslata."
                    {:adresa (first args)}))))

(rf/reg-fx
 :ws/send!
 (fn [{:keys [adrese timeout callback-event]
       :or {timeout 30000}}]
   (if callback-event
     (send! adrese timeout #(rf/dispatch (conj callback-event %)))
     (send! adrese))))


(defmulti handle-adresa
  (fn [{:keys [id]} _]
    id))

(defmethod handle-adresa :adrese/add
  [_ msg-add-event]
  (rf/dispatch msg-add-event))

(defmethod handle-adresa :adrese/creation-errors
  [_ [_ response]]
  (rf/dispatch
   [:form/set-server-errors (:errors response)]))

(defmethod handle-adresa :chsk/handshake
  [{:keys [event]} _]
  (.log js/console "Connection Established: " (pr-str event)))

(defmethod handle-adresa :chsk/state
  [{:keys [event]} _]
  (.log js/console "State Changed: " (pr-str event)))

(defmethod handle-adresa :default
  [{:keys [event]} _]
  (.warn js/console "Unknown websocket message: " (pr-str event)))


(defn receive-adresa!
  [{:keys [id event] :as ws-adresa}]
  (do
    (.log js/console "Event Received: " (pr-str event))
    (handle-adresa ws-adresa event)))

(defstate channel-router
  :start (sente/start-chsk-router!
          (:ch-recv @socket)
          #'receive-adresa!)
  :stop (when-let [stop-fn @channel-router]
          (stop-fn)))
