(ns app.view.semantic
  (:require [reagent.core :as r :refer [atom as-element]]
            [reagent.dom :as rdom]
            ["semantic-ui-react" :as semanticUIReact]
            ["react-textarea-autosize" :as TextAreaResize]
            goog.object))

(defn ob-get
  "Get an object the JS way:

    (ob-get \"Button\")
    (ob-get \"Menu\" \"Item\")"
  [lib k & ks]
  (if (seq ks)
    (apply goog.object/getValueByKeys lib k ks)
    (goog.object/get lib k)))

(defn semantic [& cls]
  (->> cls
       (apply ob-get semanticUIReact)
       r/adapt-react-class))

(def accordion (semantic "Accordion"))
(def accordion-title (semantic "Accordion" "Title"))
(def accordion-content (semantic "Accordion" "Content"))

(def grid (semantic "Grid"))
(def row (semantic "Grid" "Row"))
(def column (semantic "Grid" "Column"))

(def input (semantic "Input"))
(def button (semantic "Button"))
(def button-or (semantic "Button" "Or"))
(def button-group (semantic "Button" "Group"))
(def checkbox (semantic "Checkbox"))

(def modal (semantic "Modal"))
(def modal-header (semantic "Modal" "Header"))
(def modal-actions (semantic "Modal" "Actions"))
(def modal-content (semantic "Modal" "Content"))

(def statistic (semantic "Statistic"))
(def statistic-value (semantic "Statistic" "Value"))
(def statistic-label (semantic "Statistic" "Label"))

(def search (semantic "Search"))
(def select (semantic "Select"))

(def popup (semantic "Popup"))
(def container (semantic "Container"))

(def dimmer (semantic "Dimmer"))
(def loader (semantic "Loader"))

(def rating (semantic "Rating"))

(def tab (semantic "Tab"))
(def tab-pane (semantic "Tab" "Pane"))

(defn pane [name attached? body]
  {:menuItem name
   :render #(as-element [tab-pane {:attached attached?} body])})

(def menu (semantic "Menu"))
(def menu-menu (semantic "Menu" "Menu"))
(def menu-item (semantic "Menu" "Item"))

(def segment (semantic "Segment"))
(def sidebar (semantic "Sidebar"))
(def sidebar-pusher (semantic "Sidebar" "Pusher"))
(def sidebar-pushable (semantic "Sidebar" "Pushable"))
(def icon (semantic "Icon"))

(def card (semantic "Card"))
(def card-group (semantic "Card" "Group"))
(def card-content (semantic "Card" "Content"))
(def card-header (semantic "Card" "Header"))
(def card-meta (semantic "Card" "Meta"))
(def card-description (semantic "Card" "Description"))

(def header (semantic "Header"))
(def header-content (semantic "Header" "Content"))
(def header-subheader (semantic "Header" "Subheader"))

(def label (semantic "Label"))
(def label-detail (semantic "Label" "Detail"))
(def image (semantic "Image"))
(def divider (semantic "Divider"))

(def breadcrumb (semantic "Breadcrumb"))
(def breadcrumb-section (semantic "Breadcrumb" "Section"))
(def breadcrumb-divider (semantic "Breadcrumb" "Divider"))

(def table (semantic "Table"))
(def tr (semantic "Table" "Row"))
(def td (semantic "Table" "Cell"))
(def tbody (semantic "Table" "Body"))
(def thead (semantic "Table" "Header"))
(def th (semantic "Table" "HeaderCell"))

(def dropdown (semantic "Dropdown"))
(def dropdown-menu (semantic "Dropdown" "Menu"))
(def dropdown-item (semantic "Dropdown" "Item"))
(def dropdown-header (semantic "Dropdown" "Header"))
(def dropdown-divider (semantic "Dropdown" "Divider"))

(def form (semantic "Form"))
(def form-group (semantic "Form" "Group"))
(def form-select (semantic "Form" "Select"))
(def form-field (semantic "Form" "Field"))

(def message (semantic "Message"))
(def message-header (semantic "Message" "Header"))
(def message-item (semantic "Message" "Item"))

(def step (semantic "Step"))
(def step-group (semantic "Step" "Group"))
(def step-title (semantic "Step" "Title"))
(def step-content (semantic "Step" "Content"))
(def step-description (semantic "Step" "Description"))

(def feed (semantic "Feed"))
(def feed-event (semantic "Feed" "Event"))
(def feed-label (semantic "Feed" "Label"))
(def feed-summary (semantic "Feed" "Summary"))
(def feed-like (semantic "Feed" "Like"))
(def feed-user (semantic "Feed" "User"))
(def feed-date (semantic "Feed" "Date"))
(def feed-extra (semantic "Feed" "Extra"))
(def feed-content (semantic "Feed" "Content"))

(def transition-group (semantic "Transition" "Group"))


(defn input-field [options]
  (let [has-focus? (atom false)
        is-valid? (atom true)]
    (fn [options]
      (let [{:keys [info label read-only atom on-return-press]} options]
        ;; only show an error when we don't have focus
        [form-field {:error (and (not @has-focus?)
                                 (not @is-valid?))}
         [:label label]
         [:input.ui.input {:type          (if (:password? options) "password" "text")
                           :name          (or (:name options) label)
                           :value         (or @atom "")
                           :read-only     read-only
                           :on-key-press  (fn [ev]
                                            (when (and on-return-press (= (.-key ev) "Enter"))
                                              (on-return-press ev)))
                           :placeholder   (:placeholder options)
                           :on-focus      #(reset! has-focus? true)
                           :on-blur       #(reset! has-focus? false)
                           :on-change     #(reset! atom (-> % .-target .-value))}]
         (when icon
           [:i {:aria-hidden true :class ["icon" icon]}])
         (when info [message {:info true :icon "info" :content info}])]))))

(defn tb [atom]
  (let [ta (.-default TextAreaResize)]
    (r/create-class
     {:reagent-render
      (fn [atom]
        [:> ta {:default-value @atom
                :on-change #(reset! atom (-> % .-target .-value))}])

      :component-did-update
      (fn [this [_ old]]
        (let [new (first (rest (r/argv this)))
              area (rdom/dom-node this)]
          (cond
            (nil? @new)
            (set! (.-value area) "")

            (not= @old @new)
            (set! (.-value area) @new))))})))

;; This will automatically resize to the size on the input text
(defn textarea [{:keys [atom label info]}]
  [form-field
   [:label label]
   (when info [:small.info info])
   [tb atom]])

(defn month-year [{:keys [atom on-goable? label]}]
  [:<>
   [:b [:label label]]
   (when-not (:on-going? @atom)
     [form-group {:widths 2}
      [form-field
       [select {:options [{:key :jan :value :jan :text "January"}
                          {:key :feb :value :feb :text "February"}
                          {:key :mar :value :mar :text "March"}
                          {:key :apr :value :apr :text "April"}
                          {:key :may :value :may :text "May"}
                          {:key :jun :value :jun :text "June"}
                          {:key :jul :value :jul :text "July"}
                          {:key :aug :value :aug :text "August"}
                          {:key :sep :value :sep :text "September"}
                          {:key :oct :value :oct :text "October"}
                          {:key :nov :value :nov :text "November"}
                          {:key :dec :value :dec :text "December"}]
                :value (:month @atom)
                :on-change #(swap! atom assoc :month (keyword (:value (js->clj %2 :keywordize-keys true))))
                :placeholder "Month"}]]
      [form-field {:control "input"
                   :placeholder "Year"
                   :value (or (:year @atom) 1900)
                   :on-change #(swap! atom assoc :year (-> % .-target .-value))
                   :type "number"}]])
   (when on-goable?
     [form-field
      [checkbox {:toggle true
                 :checked (boolean (:on-going? @atom))
                 :on-change #(swap! atom assoc :on-going? (:checked (js->clj %2 :keywordize-keys true)))
                 :label "Currently ongoing."}]])])
