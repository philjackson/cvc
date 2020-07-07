(ns app.view.index
  (:require [app.view.semantic :as s]
            [app.debug :refer [debug?]]
            [reagent.core :refer [atom]]
            [reitit.frontend.easy :as rfe]
            [app.view.menu :as menu]
            [app.model.cv :as cv]
            [reagent.core :as r]
            [app.firebase.files :as files]
            [app.model.state :as state]
            [app.view.cv :refer [cv-view]]))

(defn loader [msg]
  [s/dimmer {:inverted true :active true}
   [s/loader {:size "massive"} msg]])

(defn front-page [params]
  [:<>
   [menu/menu params]
   [:div.front-page.card
    [:div.section.one
     [:div.summary
      [:h1 "It's free and easy to get started."]
      [:p
       "Next CV is a free CV generator site. It produces "
       "simple but attractive CVs ready to get you a job."]
      [:p "Watch your CV materialise in realtime as you type."]
      [s/button {:class "loud-button"
                 :on-click menu/open-login
                 :basic true
                 :size "massive"
                 :simple 1}
       "Signup to get started"]]
     [:img {:src "/img/undraw_profile_image_n3cj.svg"}]]

    [:div.section.two
     [:div.summary
      [:h1 "CV templates"]
      [:p "Pick from pre-built templates to get the look you're after."]]
     [:img {:src "/img/undraw_switches_1js3.svg"}]]

    [:div.section.three
     [:div.summary
      [:h1 "Clean styles"]
      [:p "Print your CV to a file to have a local copy or make
      physical copies that look great."]]
     [:img {:src "/img/undraw_profile_details_f8b7.svg"}]]

    [:div.section.four
     [s/button {:class "loud-button"
                :on-click menu/open-login
                :basic true
                :size "massive"
                :simple 1}
      "Signup to get started"]]]])

(defn is-tablet-width? []
  (<= (.-innerWidth js/window)
      s/tablet-width))

(defn tablet-buttons [visible-element-atom]
  [s/button {:circular true
             :size "massive"
             :primary true
             :icon (if (= @visible-element-atom :builder)
                     "file outline"
                     "edit outline")
             :class "cv-builder-switch"
             :on-click #(reset! visible-element-atom
                                (if (= @visible-element-atom :builder)
                                  :cv
                                  :builder))}])

(defonce visible-element (atom :cv))
(defn cv-and-builder [builder-view]
  (let [is-tablet? (atom (is-tablet-width?))
        window-size-fn (set! (.-onresize js/window)
                             #(reset! is-tablet? (is-tablet-width?)))]
    (fn [params]
      [:<>
       [menu/menu params]
       (when @is-tablet? [tablet-buttons visible-element])
       [:div.builder-and-cv.card
        (when (or (not @is-tablet?)
                  (and @is-tablet? (= @visible-element :builder)))
          [:div.builder
           [:div.builder-links
            (doall
             (for [item [:personal :contact :education :workexp :references]]
               ^{:key item}
               [:a {:href (rfe/href item {:cv-id (cv/selected-id @state/cvs)})
                    :class (when (= item (:selected (:data @state/match)))
                             "selected")}
                (str (name item))]))]
           [builder-view]])
        (when (or (not @is-tablet?)
                  (and @is-tablet? (= @visible-element :cv)))
          [:div#cv-column
           (when-not (:hide-pdf-message? @state/config)
             [:div.cv-message
              [s/message {:info true
                          :on-dismiss #(swap! state/config assoc :hide-pdf-message? true)
                          :icon "info"
                          :header "How to export this CV to PDF"
                          :content "Hit Ctrl-P and print to file for a PDF version."}]])
           [:div.cv-toolbar
            (let [selected (cv/selected @state/cvs)]
              [:<>
               [s/transition-group {:animation "fade up" :duration 200}
                (when (:public? selected)
                  [:a {:href (rfe/href :public {:cv-id (:public-id selected)})
                       :target "_blank"
                       :title "This is the link to your public CV"}
                   [s/icon {:name "linkify"}]])]
               [:label {:for "public-check"} "Generate a publicly available link"]
               [s/checkbox
                {:id "public-check"
                 :on-change (fn [e]
                              (let [is-checked? (.. e -target -checked)
                                    new-structure (assoc selected :public? is-checked?)]

                                ;; if the user un-checks (and there's a
                                ;; public-id), then we need to delete the
                                ;; public file
                                (swap! state/cvs
                                       assoc-in
                                       (cv/active-cv-path @state/cvs)
                                       (if (and (false? is-checked?) (:public-id selected))
                                         (do
                                           (files/delete-file!
                                            (files/get-public-filename
                                             (:public-id new-structure)))
                                           (assoc new-structure :public-id (random-uuid)))
                                         ;; They've checked the box, add a public
                                         ;; id and it'll get uploaded in the next
                                         ;; tick
                                         (assoc new-structure :public-id (random-uuid))))))
                 :checked (boolean (:public? selected))
                 :toggle true}]])]

           [:div#cv
            [cv-view params (r/cursor state/cvs (cv/active-cv-path @state/cvs))]]])]
       (when debug?
         [:<>
          [:pre (with-out-str (cljs.pprint/pprint @state/cvs))]
          [:button {:on-click (fn []
                                (rfe/push-state :index)
                                (reset! state/cvs (let [new-id (random-uuid)]
                                                    (-> (state/initial-state)
                                                        :cvs
                                                        (cv/add {:id new-id :name "Main"})
                                                        (cv/select new-id)))))}
           "Delete all data"]])])))

(defn public-cv [params]
  [:<>
   [:a.generated-message {:href "/"} "Generated using Next CV"]
   [:div#cv.public
    (if (:selected @state/cvs)
      [cv-view params (r/cursor state/cvs (cv/active-cv-path @state/cvs))]
      [loader "Loading CV..."])]
   (when debug?
     [:pre (with-out-str (cljs.pprint/pprint @state/all-seeing-state))])])

(defn index-dispatcher [builder-view]
  (fn [params]
    (let [user @state/user
          cvs @state/cvs]
      (cond
        ;; we've tried to load a user, but there isn't one
        (and (map? user) (empty? user))
        [front-page]

        (not (:selected cvs))
        [loader "Loading your CVs..."]

        ;; we have all of our data, load the view, giving it a (css)
        ;; class name and passing in any parameters for convenience
        :else
        [cv-and-builder builder-view]))))
