(ns nekretnine.websockets
  (:require [cljs.reader :as edn]))

(defonce channel (atom nil))

(defn connect! [url receive-handler]
  (if-let [chan (js/WebSocket. url)]
    (do
      (.log js/console "Connected!")
      (set! (.-onmessage chan) #(->> %
                                     .-data
                                     edn/read-string
                                     receive-handler))
      (reset! channel chan))
    (throw (ex-info "Websocket Connection Failed!"
                    {:url url}))))

(defn send-adresa! [adr]
  (if-let [chan @channel]
    (.send chan (pr-str adr))
    (throw (ex-info "Kanal zatvoren. Adresa nije poslata."
                    {:adresa adr}))))