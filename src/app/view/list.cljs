(ns app.view.list
  (:require [reagent.core :as r :refer [atom]]
            [com.rpl.specter :refer [ALL NONE pred= selected?] :refer-macros [setval select-one]]
            ["react-beautiful-dnd" :as dnd :refer [DragDropContext Draggable Droppable]]
            [app.view.semantic :as s]))

(defn list-delete [list id]
  (setval [ALL (selected? :id (pred= id))] NONE list))

(defn list-find [list id]
  (select-one [ALL (selected? :id (pred= id))] list))

(defn list-update [list id val]
  (setval [ALL (selected? :id (pred= id))] val list))

(defn drag-end [list-atom result]
  (let [{:keys [source destination]} (js->clj result :keywordize-keys true)
        source-idx (:index source)
        dest-idx   (:index destination)]
    (when (and dest-idx (not (= dest-idx source-idx)))
      (let [source-ob  (nth @list-atom source-idx)
            dest-ob    (nth @list-atom dest-idx)]
        (reset! list-atom
                (last
                 (reduce (fn [[looped-idx new-list] looped-ob]
                           (cond
                             ;; we've matched our destination, add the
                             ;; new item with the old one again.
                             (= dest-idx looped-idx)
                             [(inc looped-idx) (if (> source-idx dest-idx)
                                                 (-> new-list
                                                     (conj source-ob)
                                                     (conj dest-ob))
                                                 (-> new-list
                                                     (conj dest-ob)
                                                     (conj source-ob)))]
                             
                             ;; another list entry
                             (not= source-idx looped-idx)
                             [(inc looped-idx) (conj new-list looped-ob)]
                             
                             ;; nothing else to do, return the new list
                             :else
                             [(inc looped-idx) new-list]))
                         [0 []]
                         @list-atom)))))))

(defn list-item [ref line-item-fn index edit-fn delete-fn]
  [:> Draggable {:draggable-id (str (:id ref))
                 :index index}
   (fn [provided _]
     (r/as-element
      [:div.list-item (merge {:ref (.-innerRef provided)}
                             (js->clj (.-draggableProps provided))
                             (js->clj (.-dragHandleProps provided)))
       [:div.grab-handle]
       [:div.list-text (line-item-fn ref)]
       [:div.list-buttons
        [s/button {:title "Edit"
                   :size "tiny"
                   :icon "edit"
                   :on-click edit-fn}]
        [s/button {:title "Delete"
                   :size "tiny"
                   :icon "delete"
                   :on-click delete-fn}]]]))])

(defn present-list [section new-item-str line-item-fn form]
  (let [updating? (atom false)
        new-item (atom {})]
    (fn [section new-item-str line-item-fn form]
      (if (not @updating?)
        ;; we do this because the @section deref in the inner-inner fn
        ;; doesn't get noticed. This changing will trigger a refresh.
        ^{:key @section}
        [:> DragDropContext {:onDragEnd (partial drag-end section)}
         [:> Droppable {:droppable-id "droppable" :type "thing"}
          (fn [provided _]
            (r/as-element
             [:div (merge {:ref (.-innerRef provided)}
                          (js->clj (.-droppableProps provided))) 
              [s/button {:fluid true
                         :primary true
                         :on-click (fn []
                                     (reset! updating? true)
                                     (reset! new-item {}))}
               new-item-str]
              [:div.list
               (map-indexed (fn [i ref]
                              ^{:key ref} [list-item ref
                                           line-item-fn
                                           i
                                           (fn []
                                             (reset! new-item (list-find @section (:id ref)))
                                             (reset! updating? true))
                                           #(reset! section (list-delete @section (:id ref)))])
                            @section)
               (.-placeholder provided)]]))]]

        ;; form for editing
        [:div.list-editor
         [s/form {:on-submit (fn []
                               (if (:id @new-item)
                                 ;; we're updating this item
                                 (reset! section (list-update @section
                                                              (:id @new-item)
                                                              @new-item))
                                 ;; we're adding a brand new item
                                 (swap! section conj (assoc @new-item :id (random-uuid))))
                               (reset! new-item {})
                               (reset! updating? false))}
          [form new-item]
          [s/button  {:type "submit" :primary true} "Save details"]
          [s/button {:type "reset" :on-click #(reset! updating? false)} "Cancel"]]]))))
