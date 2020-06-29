(ns app.firebase.files
  #:ghostwheel.core{:check true :num-tests 10}
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            ["firebase/storage"]
            [app.model.cv :as cv]
            [app.model.state :as state]
            [cognitect.transit :as transit]
            [com.cognitect.transit.types]
            [ghostwheel.core :as g :refer [>defn =>]]))

;; UUID support for transit
(extend-type com.cognitect.transit.types/UUID IUUID)

(>defn get-private-filename
  [uid]
  [string? => string?]
  (clojure.string/join "/"
                       ["private"
                        (.. js/window
                            -location
                            -hostname)
                        uid
                        "cvs.edn"]))

(>defn get-public-filename
  [public-id]
  [::state/id => string?]
  (clojure.string/join "/"
                       ["public"
                        (.. js/window
                            -location
                            -hostname)
                        (str public-id ".edn")]))

(defn thing->blob
  "Convert the passed in cv-state to a Javascript Blob, ready to
  upload."
  [obj]
  (js/Blob. [(transit/write (transit/writer :json) obj)]
            #js {:type "application/edn;charset=utf-8"}))

(defn upload-file! [data filename]
  (let [ref (.child (.ref (.storage firebase)) filename)
        blob (thing->blob data)]
    (-> (.put ref blob)
        (.then (fn [snapshot]
                 (let [bytes (.-bytesTransferred snapshot)]
                   (print (str filename " uploaded, " bytes "bytes.")))))
        (.catch (fn [e] (.log js/console e))))))

(defn upload-all-files! []
  (let [user     @state/user
        cv-state @state/cvs
        public   (cv/public-cvs cv-state)]
    ;; private first
    (upload-file! @state/cvs (get-private-filename (:uid user)))

    ;; for each new document we just upload the CV, _not_the CV
    ;; state (with :selected etc.)
    (doseq [cv public]
      (upload-file! cv (get-public-filename (:public-id cv))))))

(defn download-file [filename on-loaded]
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
                (on-loaded nil)))))
