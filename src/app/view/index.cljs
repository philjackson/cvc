(ns app.view.index
  (:require [app.view.semantic :as s]
            [app.debug :refer [debug?]]
            [app.view.menu :as menu]
            [app.model.cv :as cv]
            [reitit.frontend.easy :as rfe]
            [reagent.core :as r]
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

(defn index [builder-view]
  (fn [params]
    [:<>
     [menu/menu params]
     [:div.builder-and-cv.card
      [:div.builder
       [:div.builder-links
        (doall
         (for [item [:personal :contact :education :workexp :references]]
           ^{:key item}
           [:a {:href (rfe/href item {:cv-id (cv/selected @state/cvs)})
                :class (when (= item (:selected (:data @state/match)))
                         "selected")}
            (str (name item))]))]
       [builder-view]]
      [:div#cv-column
       [:div.cv-toolbar
        [:label {:for "public-check"} "Make this CV public"]
        (let [selected-id (cv/selected @state/cvs)]
          [s/checkbox
           {:id "public-check"
            :on-change (fn [e]
                         (let [is-checked? (.. e -target -checked)]
                           (swap! state/cvs
                                  assoc-in
                                  [:docs selected-id]
                                  (let [selected (cv/cv-get @state/cvs selected-id)]
                                    (cond-> selected
                                      true
                                      (assoc :public? is-checked?)

                                      ;; TODO delete public file here...
                                      (= false is-checked?)
                                      (identity)

                                      ;; if we don't have a public id, make one
                                      (not (:public-id selected))
                                      (assoc :public-id (random-uuid)))))))
            :checked (boolean (:public? (cv/cv-get @state/cvs selected-id)))
            :toggle true}])]

       [:div#cv
        [cv-view params (r/cursor state/cvs (cv/active-cv-path @state/cvs))]]]]
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
         "Delete all data"]])]))
