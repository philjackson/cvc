(ns app.firebase.files
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            ["firebase/storage"]
            [ghostwheel.core :as g :refer [>defn =>]]
            [cognitect.transit :as transit]
            [reagent.core :as r :refer [atom]]
            [app.model.cv :refer [details->blob]]
            [app.firebase.config :refer [firebase-config]]))

(>defn get-filename
  [uid]
  [:app.model.cv/id => string?]
  (clojure.string/join "/"
                       ["private"
                        (.. js/window
                            -location
                            -hostname)
                        uid
                        "cvs.edn"]))

(defn upload-file [details uid]
  (let [filename (get-filename uid)
        ref (.child (.ref (.storage firebase)) filename)
        blob (details->blob details)]
    (-> (.put ref blob)
        (.then (fn [snapshot]
                 (let [bytes (.-bytesTransferred snapshot)]
                   (print (str filename " uploaded, " bytes "bytes.")))))
        (.catch (fn [e] (.log js/console e))))))

(defn download-file [uid on-loaded]
  (let [filename (get-filename uid)]
    (-> (.getDownloadURL (.child (.ref (.storage firebase)) filename))
        (.then (fn [url]
                 (let [xhr (js/XMLHttpRequest.)]
                   (set! (.-onload xhr) #(on-loaded (transit/read
                                                     (transit/reader :json)
                                                     (.-response xhr))))
                   (.open xhr "GET" url, true)
                   (.send xhr))))
        (.catch (fn [e] (.log js/console e))))))
