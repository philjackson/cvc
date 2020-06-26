(ns app.firebase.files
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            ["firebase/storage"]
            [app.model.state :as state]
            [app.model.cv :refer [cv->blob]]
            [cognitect.transit :as transit]
            [com.cognitect.transit.types]
            [app.firebase.config :refer [firebase-config]]))

;; UUID support for transit
(extend-type com.cognitect.transit.types/UUID IUUID)

(defn get-private-filename [uid]
  (clojure.string/join "/"
                       ["private"
                        (.. js/window
                            -location
                            -hostname)
                        uid
                        "cvs.edn"]))

(defn upload-file! [cv-state filename]
  (let [ref (.child (.ref (.storage firebase)) filename)
        blob (cv->blob cv-state)]
    (-> (.put ref blob)
        (.then (fn [snapshot]
                 (let [bytes (.-bytesTransferred snapshot)]
                   (print (str filename " uploaded, " bytes "bytes.")))))
        (.catch (fn [e] (.log js/console e))))))

(defn upload-files! [cv-state]
  (let [user @state/user
        ]
    ;; private first
    (upload-file! @state/cvs (get-private-filename (:uid user)))

    ))

(defn download-file [uid on-loaded]
  (let [filename (get-private-filename uid)]
    (-> (.getDownloadURL (.child (.ref (.storage firebase)) filename))
        (.then (fn [url]
                 (let [xhr (js/XMLHttpRequest.)]
                   (set! (.-onload xhr) #(on-loaded (transit/read
                                                     (transit/reader :json)
                                                     (.-response xhr))))
                   (.open xhr "GET" url, true)
                   (.send xhr))))
        (.catch (fn [e]
                  (when-not (= (.-code e) "storage/object-not-found")
                    (.log js/console e))
                  (on-loaded nil))))))
