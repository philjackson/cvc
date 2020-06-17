(ns app.view.variation-switcher
  (:require [app.view.semantic :as s]
            [reitit.frontend.easy :as rfe]
            [app.model.state :as state]
            [app.model.cv :as cv]
            [reagent.core :as r :refer [atom]]))

#_(defn manage-modal [state/cvs open?]
    (let [new-name (atom nil)
          close (fn []
                  (reset! new-name nil)
                  (reset! open? false))]
      (fn [state/cvs open?]
        [s/modal {:on-close close
                     :open @open?
                     :size "small"
                     :close-icon true}
         [s/modal-header "Manage CVs"]
         [s/modal-content
          [:div.list
           (doall
            (for [cv (vals (:cvs @state/cvs))]
              ^{:key (:id cv)}
              [:div.list-item
               [:a {:href (rfe/href :cv-indexed {:id (:id cv)})} (:name cv)]
               [:div.list-buttons
                ;; can't delete the active cv
                [s/button {:title "Delete"
                              :disabled (= (:name (selected @state/cvs)) (:name cv))
                              :size "tiny"
                              :on-click #(reset! state/cvs (delete-cv @state/cvs (:id cv)))
                              :icon "delete"}]]]))]
          [s/divider]
          [s/modal-actions
           [s/button {:primary true :on-click close} "Done"]
           [s/button {:negative true :on-click
                         (fn []
                           (let [the-new-old (initial-state)]
                             (reset! state/cvs the-new-old)
                             (rfe/push-state :cv-indexed {:id (:id (selected @state/cvs))}))
                           (close))}
            "Delete all data!"]]]])))

#_(defn add-modal [state/cvs open?]
    (let [new-name (atom nil)
          base-on-open? (atom false)
          base-on (atom (:id (selected @state/cvs)))
          close (fn []
                  (reset! base-on-open? false)
                  (reset! new-name nil)
                  (reset! open? false))]
      (fn [state/cvs open?]
        [s/modal {:on-close close
                  :open @open?
                  :size "small"
                  :close-icon true}
         [s/modal-header "Add a new CV"]
         [s/modal-content
          [s/form
           [s/message {:info true
                       :icon "info"
                       :header "Why multiple CVs?"
                       :content (str "Sometimes when you're applying to a particular "
                                     "company, it may be the case there are skills they "
                                     "don't want to see that others might. This system allows "
                                     "you to tailor CVs for specific roles.")}]
           [s/input-field {:atom new-name
                           :label "CV's name"}]
           [s/form-group
            [s/form-field
             [s/checkbox {:label "Base this CV on one of my others..."
                          :checked @base-on-open?
                          :on-change #(reset! base-on-open? (get (js->clj %2) "checked"))
                          :toggle true}]]]
           (when @base-on-open?
             [s/form-field
              [s/select {:label "Based on:"
                         :value @base-on
                         :on-change #(reset! base-on (get (js->clj %2) "value"))
                         :options (doall
                                   (for [[k v] (:cvs @state/cvs)]
                                     {:key k :text (:name v) :value k}))}]])]
          [s/divider]
          [s/modal-actions
           [s/button
            {:type "submit"
             :primary true
             :disabled (boolean (not (and @new-name (count @new-name))))
             :on-click (fn []
                         (reset! state/cvs
                                 (let [new-uuid (gen-id)]
                                   (-> @state/cvs
                                       (set-selected new-uuid)
                                       (assoc-in [:cvs new-uuid]
                                                 (cond->> {:id new-uuid
                                                           :name @new-name}
                                                   @base-on-open?
                                                   (merge (get-in @state/cvs [:cvs @base-on])))))))
                         (close))}
            "Add CV"]
           [s/button {:type "submit" :on-click close} "Cancel"]]]])))

(defn select-variation []
  (let [add-modal-open? (atom false)
        manage-modal-open? (atom false)]
    (fn []
      [:<>
       #_[manage-modal state/cvs manage-modal-open?]
       #_[add-modal state/cvs add-modal-open?]
       [:div.select-variation
        [s/dropdown {:text (str "Select a CV: " (-> @state/cvs
                                                    (get-in (cv/active-cv-path @state/cvs))
                                                    :name))}
         [s/dropdown-menu
          [s/dropdown-item {:text "Add a new CV"
                            :on-click #(reset! add-modal-open? true)}]
          [s/dropdown-item {:text "Manage CVs"
                            :on-click #(reset! manage-modal-open? true)}]
          [s/dropdown-divider]
          (doall
           (for [[k v] (:docs @state/cvs)]
             ^{:key k}
             [s/dropdown-item {:text (:name v)
                               :icon (if (= (:id v) (cv/selected @state/cvs))
                                       "dot circle outline"
                                       "circle outline")
                               :on-click #(rfe/push-state :personal {:id k})}]))]]]])))
