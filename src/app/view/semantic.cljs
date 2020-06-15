(ns app.view.semantic
  (:require [reagent.core :as r :refer [atom]]
            [reagent.dom :as dom]
            ["semantic-ui-react" :as sem]
            ["react-textarea-autosize" :as TextAreaResize]
            goog.object))

(defn component* [sem-comp]
  (partial into [(r/adapt-react-class sem-comp)]))

(def component (memoize component*))

(def responsive         #((component sem/Responsive) %&))
(def only-mobile        (js->clj (.-onlyMobile      sem/Responsive) :keywordize-keys true))
(def only-tablet        (js->clj (.-onlyTablet      sem/Responsive) :keywordize-keys true))
(def only-computer      (js->clj (.-onlyComputer    sem/Responsive) :keywordize-keys true))
(def only-largeScreen   (js->clj (.-onlyLargeScreen sem/Responsive) :keywordize-keys true))
(def only-widescreen    (js->clj (.-onlyWidescreen  sem/Responsive) :keywordize-keys true))

(def accordion          #((component sem/Accordion) %&))
(def accordion-title    #((component sem/AccordionTitle) %&))
(def accordion-content  #((component sem/AccordionContent) %&))

(def grid               #((component sem/Grid) %&))
(def row                #((component sem/GridRow) %&))
(def column             #((component sem/GridColumn) %&))

(def input              #((component sem/Input) %&))
(def button             #((component sem/Button) %&))
(def button-group       #((component sem/ButtonGroup) %&))
(def checkbox           #((component sem/Checkbox) %&))

(def modal              #((component sem/Modal) %&))
(def modal-header       #((component sem/ModalHeader) %&))
(def modal-actions      #((component sem/ModalActions) %&))
(def modal-content      #((component sem/ModalContent) %&))

(def statistic          #((component sem/Statistic) %&))
(def statistic-value    #((component sem/StatisticValue) %&))
(def statistic-label    #((component sem/StatisticLabel) %&))

(def search             #((component sem/Search) %&))
(def select             #((component sem/Select) %&))

(def popup              #((component sem/Popup) %&))
(def container          #((component sem/Container) %&))

(def dimmer             #((component sem/Dimmer) %&))
(def loader*            #((component sem/Loader) %&))

(def loader-defs {:size "large" :inline "centered" :active true})
(defn loader
  ([content] (loader {} content))
  ([options content] (loader* (merge loader-defs options)
                              content)))

(def rating             #((component sem/Rating) %&))

(def tab                #((component sem/Tab) %&))
(def tab-pane           #((component sem/TabPane) %&))

(def menu               #((component sem/Menu) %&))
(def menu-menu          #((component sem/MenuMenu) %&))
(def menu-header        #((component sem/MenuHeader) %&))
(def menu-item          #((component sem/MenuItem) %&))

(def segment            #((component sem/Segment) %&))
(def sidebar            #((component sem/Sidebar) %&))
(def sidebar-pusher     #((component sem/SidebarPusher) %&))
(def sidebar-pushable   #((component sem/SidebarPushable) %&))
(def icon               #((component sem/Icon) %&))

(def card               #((component sem/Card) %&))
(def card-group         #((component sem/CardGroup) %&))
(def card-content       #((component sem/CardContent) %&))
(def card-header        #((component sem/CardHeader) %&))
(def card-meta          #((component sem/CardMeta) %&))
(def card-description   #((component sem/CardDescription) %&))

(def header             #((component sem/Header) %&))
(def header-content     #((component sem/HeaderContent) %&))
(def header-subheader   #((component sem/HeaderSubheader) %&))

(def label              #((component sem/Label) %&))
(def label-detail       #((component sem/LabelDetail) %&))
(def image              #((component sem/Image) %&))
(def divider            #((component sem/Divider) %&))

(def breadcrumb         #((component sem/Breadcrumb) %&))
(def breadcrumb-section #((component sem/BreadcrumbSection) %&))
(def breadcrumb-divider #((component sem/BreadcrumbDivider) %&))

(def table              #((component sem/Table) %&))
(def tr                 #((component sem/TableRow) %&))
(def td                 #((component sem/TableCell) %&))
(def tbody              #((component sem/TableBody) %&))
(def thead              #((component sem/TableHeader) %&))
(def th                 #((component sem/TableHeaderCell) %&))

(def dropdown           #((component sem/Dropdown) %&))
(def dropdown-menu      #((component sem/DropdownMenu) %&))
(def dropdown-item      #((component sem/DropdownItem) %&))
(def dropdown-header    #((component sem/DropdownHeader) %&))
(def dropdown-divider   #((component sem/DropdownDivider) %&))

(def form               #((component sem/Form) %&))
(def form-group         #((component sem/FormGroup) %&))
(def form-select        #((component sem/FormSelect) %&))
(def form-field         #((component sem/FormField) %&))

(def message            #((component sem/Message) %&))
(def message-header     #((component sem/MessageHeader) %&))
(def message-item       #((component sem/MessageItem) %&))

(def step               #((component sem/Step) %&))
(def step-group         #((component sem/StepGroup) %&))
(def step-title         #((component sem/StepTitle) %&))
(def step-content       #((component sem/StepContent) %&))
(def step-description   #((component sem/StepDescription) %&))

(def feed               #((component sem/Feed) %&))
(def feed-event         #((component sem/FeedEvent) %&))
(def feed-label         #((component sem/FeedLabel) %&))
(def feed-summary       #((component sem/FeedSummary) %&))
(def feed-like          #((component sem/FeedLike) %&))
(def feed-user          #((component sem/FeedUser) %&))
(def feed-date          #((component sem/FeedDate) %&))
(def feed-extra         #((component sem/FeedExtra) %&))
(def feed-content       #((component sem/FeedContent) %&))

(def transition         #((component sem/Transition) %&))

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
      (fn [this [_ atom]]
        (let [area (dom/dom-node this)]
          (when-not (= @atom (.-value area))
            (set! (.-value area) @atom))))})))

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
