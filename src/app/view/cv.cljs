(ns app.view.cv
  (:require ["markdown-it" :as Markdown]
            [app.truthy :refer [truthy?]]
            [app.view.semantic :refer [icon]]))

(defonce md (new Markdown))
(defn markdown [content]
  {:dangerouslySetInnerHTML
   {:__html (.render md content)}})

(defn strip-down-href [href]
  (clojure.string/replace href
                          #"https?://(www.)?"
                          ""))

(defn date [start finish]
  (cond-> [:span.date]
    (:month start)
    (conj [:span.month (:month start)])

    (:year start)
    (->
     (conj " ")
     (conj [:span.year (:year start)]))

    (or (:month finish) (:year finish) (:on-going? finish))
    (conj " - ")

    (:on-going? finish)
    (conj "Ongoing")

    (:month finish)
    (conj [:span.month (:month finish)])

    (:year finish)
    (->
     (conj " ")
     (conj [:span.year (:year finish)]))))

(defn references [atm]
  [:div#references
   (for [{:keys [id name position comments]} (:references @atm)]
     ^{:key id}
     [:div.reference
      [:h3.name name]
      [:div.position position]
      [:div.comments comments]])])

(defn work-exp [atm]
  [:div#workexp
   (doall
    (for [{:keys [id name position start finish comments]} (:workexp @atm)]
      ^{:key id}
      [:div
       [:h3.name name " - " position]
       [:div [date start finish]]
       [:div.comments (markdown comments)]]))])

(defn education [atm]
  [:div#education
   (doall
    (for [{:keys [id institution qualification start finish]} (:education @atm)]
      ^{:key id}
      [:div
       [:h3.institution institution ]
       [:h4 qualification " - " [date start finish]]]))])

(defn contact-details [atm]
  [:div#contact-details
   [:div.personal
    (when (truthy? (:phone @atm))
      [:div.icon-pair
       [:div [icon {:name "phone"}]]
       [:a {:href (str "tel:" (:phone @atm))} (:phone @atm)]])
    (when (truthy? (:email @atm))
      [:div.icon-pair
       [:div [icon {:name "mail outline"}]]
       [:a {:href (str "mailto:" (:email @atm))} (:email @atm)]])
    (when (truthy? (:website @atm))
      [:div.icon-pair
       [:div [icon {:name "sitemap"}]]
       [:a {:href (:website @atm)} (strip-down-href (:website @atm))]])]
   [:div.social
    (when (truthy? (:github @atm))
      [:div.icon-pair
       [:div [icon {:name "github"}]]
       [:a {:href (:github @atm)} (strip-down-href (:github @atm))]])
    (when (truthy? (:gitlab @atm))
      [:div.icon-pair
       [:div [icon {:name "gitlab"}]]
       [:a {:href (:gitlab @atm)} (strip-down-href (:gitlab @atm))]])
    (when (truthy? (:linkedin @atm))
      [:div.icon-pair
       [:div [icon {:name "linkedin"}]]
       [:a {:href (:linkedin @atm)} (strip-down-href (:linkedin @atm))]])
    (when (truthy? (:deviantart @atm))
      [:div.icon-pair
       [:div [icon {:name "deviantart"}]]
       [:a {:href (:deviantart @atm)} (strip-down-href (:deviantart @atm))]])
    (when (truthy? (:fivehunpx @atm))
      [:div.icon-pair
       [:div [icon {:name "500px"}]]
       [:a {:href (:fivehunpx @atm)} (strip-down-href (:fivehunpx @atm))]])]])

(defn cv-view [params atm]
  [:<>
   (when (truthy? (:full-name @atm))
     [:header
      [:h1#full-name (:full-name @atm)]
      [:h2#job-title (:job-title @atm)]
      (when (truthy? (:location @atm))
        [:div#location
         [:div [icon {:name "map marker alternate"}]]
         [:div (:location @atm)]])])
   [contact-details atm]
   (when (truthy? (:summary @atm))
     [:<>
      [:h2.section "Summary"]
      [:div#summary (markdown (:summary @atm))]])
   (when (truthy? (:workexp @atm))
     [:<>
      [:h2.section "Work Experience"]
      [work-exp atm]])
   (when (truthy? (:education @atm))
     [:<>
      [:h2.section "Education"]
      [education atm]])
   (when (truthy? (:references @atm))
     [:<>
      [:h2.section "References"]
      [references atm]])])
