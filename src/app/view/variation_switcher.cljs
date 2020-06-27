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
             [s/button {:title "Edit"
                        :size "tiny"
                        :on-click (fn []
                                    (rfe/push-state :personal {:cv-id id})
                                    (close))
                        :icon "edit"}]
             [s/button {:title "Delete"
                        :disabled (= (cv/selected-id @state/cvs) id)
                        :size "tiny"
                        :on-click (fn []
                                    (reset! state/cvs (cv/delete @state/cvs id))
                                    #_"TODO delete the public CV here") 
                        :icon "delete"}]]))]
        [s/divider]
        [s/modal-actions
         [s/button {:primary true :on-click close} "Done"]]]])))

(defn add-modal [open?]
  (r/with-let [new-name (atom nil)
               base-on-open? (atom false)
               close (fn []
                       (reset! base-on-open? false)
                       (reset! new-name nil)
                       (reset! open? false))]
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
         [s/checkbox {:label (str "Base this CV on "
                                  (get-in @state/cvs
                                          (cv/active-cv-path @state/cvs :name)))
                      :checked @base-on-open?
                      :on-change #(reset! base-on-open? (get (js->clj %2) "checked"))}]]]]
      [s/divider]
      [s/modal-actions
       [s/button
        {:type "submit"
         :primary true
         :disabled (boolean (not (and @new-name (count @new-name))))
         :on-click (fn []
                     (let [new-id (random-uuid)
                           new-cv (if @base-on-open?
                                    (cv/cv-merge
                                     (cv/cv-get @state/cvs (cv/selected-id @state/cvs))
                                     {:id new-id
                                      :public? false
                                      :name @new-name})
                                    {:id new-id :name @new-name})]
                       (reset! state/cvs (-> @state/cvs
                                             (cv/add new-cv)
                                             (cv/select new-id))))
                     (close))}
        "Add CV"]
       [s/button {:type "submit" :on-click close} "Cancel"]]]]))

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
                               :icon (if (= (:id v) (cv/selected-id @state/cvs))
                                       "dot circle outline"
                                       "circle outline")
                               :on-click #(rfe/push-state :personal {:cv-id k})}]))]]]])))
