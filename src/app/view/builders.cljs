(ns app.view.builders
  (:require [app.view.semantic :as s]
            [app.model.state :as state]
            [app.model.cv :refer [active-cv-path]]
            [reagent.core :as r]
            [app.view.list :refer [present-list]]))

(defn personal []
  [s/form
   [:h2 "Personal:"]
   [:<>
    [s/input-field {:atom (r/cursor state/cvs (active-cv-path @state/cvs :full-name))
                    :label "Full name:"}]
    [s/input-field {:atom (r/cursor state/cvs (active-cv-path @state/cvs :location))
                    :placeholder "London, UK"
                    :label "Location:"}]
    [s/input-field {:atom (r/cursor state/cvs (active-cv-path @state/cvs :job-title))
                    :label "Job title:"
                    :placeholder "CFO, advisor"}]
    [s/textarea {:atom (r/cursor state/cvs (active-cv-path @state/cvs :summary))
                 :info "Basic Markdown is supported in this field."
                 :label "Summary text:"}]]])

(defn workexp []
  (let [section (r/cursor state/cvs (active-cv-path @state/cvs :workexp))]
    (fn []
      [:<>
       [:h2 "Work experience:"]
       [present-list section
        "Add new work experience item"
        #(str (:name %) " - " (:position %))
        (fn [temp-atom]
          [:div
           [s/input-field {:atom (r/cursor temp-atom [:name])
                           :label "Workplace name:"}]
           [s/input-field {:atom (r/cursor temp-atom [:position])
                           :label "Position within company:"}]
           [s/month-year {:atom (r/cursor temp-atom [:start])
                          :label "Date started:"}]
           [s/month-year {:atom (r/cursor temp-atom [:finish])
                          :label "Date finished:"
                          :on-goable? true}]
           [s/textarea {:atom (r/cursor temp-atom [:comments])
                        :info "Basic Markdown is supported in this field."
                        :label "Description:"}]])]])))

(defn education []
  (let [section (r/cursor state/cvs (active-cv-path @state/cvs :education))]
    (fn []
      [:<>
       [:h2 "Education:"]
       [present-list
        section
        "Add new institution"
        #(str (:institution %) " - " (:qualification %))
        (fn [temp-atom]
          [:<>
           [s/input-field {:atom  (r/cursor temp-atom [:institution])
                           :label "Institution:"}]
           [s/input-field {:atom  (r/cursor temp-atom [:qualification])
                           :label "Qualification:"
                           :placeholder "BA, Corp. Finance"}]
           [s/month-year {:atom (r/cursor temp-atom [:start])
                          :label "Start:"}]
           [s/month-year {:atom (r/cursor temp-atom [:finish])
                          :label "Finish:"
                          :on-goable? true}]])]])))

(defn references []
  (let [section (r/cursor state/cvs (active-cv-path @state/cvs :references))]
    (fn []
      [:<>
       [:h2 "References:"]
       [present-list section
        "Add new reference"
        #(str (:name %) " - " (:position %))
        (fn [temp-atom]
          [:div
           [s/input-field {:atom (r/cursor temp-atom [:name])
                           :label "Referee Name:"}]
           [s/input-field {:atom (r/cursor temp-atom [:position])
                           :label "Referee Position/company:"}]
           [s/textarea {:atom (r/cursor temp-atom [:comments])
                        :label "Comments:"}]])]])))

(defn contact []
  [:> s/form
   [:h2 "General contact state/cvs:"]
   [s/input-field {:atom  (r/cursor state/cvs (active-cv-path @state/cvs :email))
                   :label "Email:"}]
   [s/input-field {:atom  (r/cursor state/cvs (active-cv-path @state/cvs :website))
                   :label "Website:"}]
   [s/input-field {:atom  (r/cursor state/cvs (active-cv-path @state/cvs :phone))
                   :label "Phone:"}]
   [:h2 "Social contact state/cvs:"]
   [s/input-field {:atom  (r/cursor state/cvs (active-cv-path @state/cvs :linkedin))
                   :label "Linkedin:"}]
   [s/input-field {:atom  (r/cursor state/cvs (active-cv-path @state/cvs :gitlab))
                   :label "Gitlab:"}]
   [s/input-field {:atom  (r/cursor state/cvs (active-cv-path @state/cvs :github))
                   :label "Github:"}]
   [s/input-field {:atom  (r/cursor state/cvs (active-cv-path @state/cvs :deviantart))
                   :label "Deviantart:"}]
   [s/input-field {:atom  (r/cursor state/cvs (active-cv-path @state/cvs :fivehunpx))
                   :label "500px:"}]])
