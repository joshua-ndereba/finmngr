

(ns finmngr.core
  (:require [clojure.data.json :as json]
            [seesaw.core :as sc]
            [seesaw.event :as ev])
  (:import (javax.swing JOptionPane)))

;; Global state for transactions
(def transactions (atom []))

;; Function to save transactions to a JSON file
(defn save-transactions-to-file []
  (spit "transactions.json" (json/write-str @transactions :escape-unicode true)))

;; Function to load transactions from a JSON file
(defn load-transactions-from-file []
  (try
    (reset! transactions (vec (json/read-str (slurp "transactions.json") :key-fn keyword)))
    (catch Exception e
      (println "Error loading transactions from file:" (.getMessage e)))))

;; GUI for adding a transaction
(defn show-add-transaction []
  (let [type-field (sc/combobox :model ["income" "expense"])
        amount-field (sc/text :text "")
        category-field (sc/text :text "")
        description-field (sc/text :text "")
        panel (sc/grid-panel :columns 2
                              :items ["Type" type-field
                                      "Amount" amount-field
                                      "Category" category-field
                                      "Description" description-field])
        dialog (sc/dialog :title "Add Transaction" 
                          :content panel 
                          :modal? true 
                          :size [500 :by 200])] ;; Set dialog size here
    ;; Show the dialog and wait for user input
    (sc/show! dialog)
    (when (= (JOptionPane/showConfirmDialog dialog "Confirm Transaction?" "Confirm" JOptionPane/OK_CANCEL_OPTION) JOptionPane/OK_OPTION)
      (let [new-transaction {:type (sc/value type-field)
                             :amount (Double/parseDouble (sc/value amount-field))
                             :category (sc/value category-field)
                             :description (sc/value description-field)
                             :timestamp (str (java.time.LocalDateTime/now))}]
        (swap! transactions conj new-transaction)
        (save-transactions-to-file)
        (sc/alert "Transaction added successfully!")))
    (sc/hide! dialog)))

;; GUI for viewing transactions in a list view
(defn show-transactions []
  (let [list-view (sc/listbox :model
                               (vec (map-indexed
                                     (fn [idx t]
                                       (str idx ": Date: " (:timestamp t)
                                            ", Type: " (:type t)
                                            ", Category: " (:category t)
                                            ", Description: " (:description t)
                                            ", Amount: Ksh " (:amount t)))
                                     @transactions)))
        panel (sc/scrollable list-view)
        frame (sc/frame :title "View Transactions"
                        :content panel
                        :width 1000
                        :height 400)]
    (sc/show! frame)))

;; GUI for showing a summary of transactions
(comment
(defn show-summary []
  (let [total-income (reduce + (map :amount (filter #(= (:type %) "income") @transactions)))
        total-expense (reduce + (map :amount (filter #(= (:type %) "expense") @transactions)))
        balance (- total-income total-expense)]
    (sc/alert (format "Summary:\nTotal Income: Ksh %.2f\nTotal Expense: Ksh %.2f\nBalance: Ksh %.2f"
                      total-income total-expense balance)))))
(defn show-summary []
  (let [total-income (reduce + (map :amount (filter #(= (:type %) "income") @transactions)))
        total-expense (reduce + (map :amount (filter #(= (:type %) "expense") @transactions)))]
    (sc/alert
      (format "Summary:\nTotal Income: %.2f\nTotal Expense: %.2f\nBalance: %.2f"
              (double total-income)
              (double total-expense)
              (double (- total-income total-expense))))))

;;def for deleting a transaction
(defn show-delete-transaction []
  (let [data (map-indexed
               (fn [idx t]
                 (str idx ": Date: " (:timestamp t)
                      ", Type: " (:type t)
                      ", Category: " (:category t)
                      ", Description: " (:description t)
                      ", Amount: Ksh " (:amount t)))
               @transactions)
        list-view (sc/listbox :model (vec data))
        panel (sc/grid-panel :columns 1 :items
                             ["Select a transaction to delete:" list-view])
        frame (sc/frame :title "Delete Transaction" :content panel :width 1000 :height 400)]
    ;; Show the frame
    (sc/show! frame)
    ;; Wait for user selection
    (sc/listen list-view :selection
               (fn [_]
                 (let [selected-index (.getSelectedIndex list-view)]
                   (when (>= selected-index 0) ;; Ensure valid selection
                     (let [confirm? (JOptionPane/showConfirmDialog
                                     nil
                                     "Are you sure you want to delete this transaction?"
                                     "Confirm Delete"
                                     JOptionPane/YES_NO_OPTION)]
                       (when (= confirm? JOptionPane/YES_OPTION)
                         ;; Remove the selected transaction
                         (swap! transactions
                                (fn [current-transactions]
                                  (vec (remove #(= % (nth current-transactions selected-index)) current-transactions))))
                         ;; Save changes to JSON file
                         (save-transactions-to-file)
                         (sc/alert "Transaction deleted successfully!")
                         (sc/hide! frame)))))))))

;; Main function
(defn -main [& args]
  ;; Load existing transactions
  (load-transactions-from-file)
  ;; Main application GUI
  (let [main-frame (sc/frame :title "Finance Manager"
                              :content
                              (sc/vertical-panel
                               :items [(sc/button :text "Add Transaction" :listen [:action (fn [_] (show-add-transaction))])
                                       (sc/button :text "View Transactions" :listen [:action (fn [_] (show-transactions))])
                                       (sc/button :text "View Summary" :listen [:action (fn [_] (show-summary))])
                                       (sc/button :text "Delete Transaction" :listen [:action (fn [_] (show-delete-transaction))])])
                              :on-close :exit
                              :width 300
                              :height 250)]
    (sc/show! main-frame)))


