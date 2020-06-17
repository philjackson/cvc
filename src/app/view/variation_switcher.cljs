(ns app.view.variation-switcher
  (:require [app.view.semantic :as s]
            [reitit.frontend.easy :as rfe]
            [app.model.state :as state]
            [app.model.cv :as cv]
            [reagent.core :as r :refer [atom]]))

(defn manage-modal [open?]
  (let [new-name (atom nil)
        close (fn []
                (reset! new-name nil)
                (reset! open? false))]
    (fn [open?]
      [s/modal {:on-close close
                :open @open?
                :size "small"
                :close-icon true}
       [s/modal-header "Create or manage CVs"]
       [s/modal-content
        [s/divider {:horizontal true} "Manage CVs"]
        [:div.list
         (doall
          (for [[id cv] (:docs @state/cvs)]
            ^{:key id}
            [:div.list-item
             [:div.list-text [:a {:href (rfe/href :personal {:cv-id id})} (:name cv)]]
             ;; can't delete the active cv
             [s/button {:title "Delete"
                        :disabled (= (cv/selected @state/cvs) id)
                        :size "tiny"
                        :on-click #(reset! state/cvs (cv/delete @state/cvs id))
                        :icon "delete"}]]))]
        [s/divider]
        [s/modal-actions
         [s/button {:primary true :on-click close} "Done"]]]])))

(defn add-modal [open?]
  (let [new-name (atom nil)
        base-on-open? (atom nil)
        base-on (atom (cv/selected @state/cvs))
        close (fn []
                (reset! base-on-open? false)
                (reset! new-name nil)
                (reset! open? false))]
    (fn [open?]
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
         [s/input-field {:atom new-name :label "CV's name"}]
         [s/form-group
          [s/form-field
           [s/checkbox {:label "Base this CV on one of my others..."
                        :checked @base-on-open?
                        :on-change #(reset! base-on-open? (get (js->clj %2) "checked"))
                        :toggle true}]]]
         (when @base-on-open?
           (let [opts (doall
                       (for [[id cv] (:docs @state/cvs)]
                         {:key id :text (:name cv) :value id}))]
             [s/form-field
              [s/select {:label "Based on:"
                         :on-change #(reset! base-on (get (js->clj %2) "value"))
                         :default-value (:value (first opts))
                         :options opts}]]))]
        [s/divider]
        [s/modal-actions
         [s/button
          {:type "submit"
           :primary true
           :disabled (boolean (not (and @new-name (count @new-name))))
           :on-click (fn []
                       (reset! state/cvs
                               (let [new-uuid (random-uuid)]
                                 (-> @state/cvs
                                     (cv/select new-uuid)
                                     (assoc-in [:docs new-uuid]
                                               (cond->> {:id new-uuid :name @new-name}
                                                 @base-on-open?
                                                 (merge (get-in @state/cvs [:docs @base-on])))))))
                       (close))}
          "Add CV"]
         [s/button {:type "submit" :on-click close} "Cancel"]]]])))

(defn select-variation []
  (let [add-modal-open? (atom false)
        manage-modal-open? (atom false)]
    (fn []
      [:<>
       [manage-modal manage-modal-open?]
       [add-modal add-modal-open?]
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
                               :on-click #(rfe/push-state :personal {:cv-id k})}]))]]]])))
