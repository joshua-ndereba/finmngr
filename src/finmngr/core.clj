(comment
(ns finmngr.core
  (:require [seesaw.core :as sc]
            [seesaw.event :as se]
            [cheshire.core :as json]))

;; In-memory store for transactions
(def transactions (atom []))

;; Function to add a new transaction
(defn add-transaction [type amount category description]
  (swap! transactions conj {:type type
                            :amount amount
                            :category category
                            :description description
                            :timestamp (java.util.Date.)}))

;; Function to calculate summary
(defn calculate-summary []
  (let [expenses (->> @transactions (filter #(= (:type %) "expense")) (map :amount) (reduce + 0))
        income   (->> @transactions (filter #(= (:type %) "income")) (map :amount) (reduce + 0))]
    {:income income
     :expenses expenses
     :balance (- income expenses)}))

;; GUI for adding a transaction
(defn show-add-transaction []
  (let [type (sc/combobox :model ["income" "expense"])
        amount (sc/text :columns 10)
        category (sc/text :columns 10)
        description (sc/text :columns 20)
        panel (sc/grid-panel :columns 2 :items
                              ["Type:" type
                               "Amount:" amount
                               "Category:" category
                               "Description:" description])]
    (when (= (JOptionPane/showConfirmDialog nil panel "Add Transaction" JOptionPane/OK_CANCEL_OPTION)
             JOptionPane/OK_OPTION)
      (try
        (add-transaction (sc/selection type)
                         (Double/parseDouble (sc/text amount))
                         (sc/text category)
                         (sc/text description))
        (sc/alert "Transaction added successfully!")
        (catch Exception e
          (sc/alert "Invalid input. Please try again!"))))))

;; GUI for listing transactions
(defn show-transactions []
  (sc/log @transactions)  ;; Log transactions for debugging
  (let [data (map #(vector (:timestamp %) (:type %) (:category %) (:description %) (:amount %)) @transactions)
        columns ["Date" "Type" "Category" "Description" "Amount"]
        table (sc/table :model [columns data])]
    (let [frame (sc/frame :title "Transactions" :content table :width 1000 :height 1000)]
      (sc/show! table)
      (sc/show! frame))))

;; GUI for summary
(defn show-summary []
  (let [{:keys [income expenses balance]} (calculate-summary)
        panel (sc/grid-panel :columns 1 :items
                              [(str "Total Income: ksh" income)
                               (str "Total Expenses: ksh" expenses)
                               (str "Net Balance: ksh" balance)])
        frame (sc/frame :title "Summary" :content panel :width 300 :height 250)]
    (sc/show! frame)))

;; Main GUI
(defn -main [& args]
  (let [main-frame (sc/frame :title "Finance Manager"
                              :content
                              (sc/vertical-panel
                                :items [(sc/button :text "Add Transaction" :listen [:action (fn [_] (show-add-transaction))])
                                        (sc/button :text "View Transactions" :listen [:action (fn [_] (show-transactions))])
                                        (sc/button :text "View Summary" :listen [:action (fn [_] (show-summary))])])
                              :on-close :exit
                              :width 300 :height 200)]
    (sc/show! main-frame)))







(ns finmngr.core
  (:require [seesaw.core :as sc]
            [seesaw.event :as se]
            [cheshire.core :as json])  ; Optional for JSON handling
  (:import [javax.swing JOptionPane])
  (:gen-class))

;; In-memory store for transactions
(def transactions (atom []))

;; Function to add a new transaction
(defn add-transaction [type amount category description]
  (swap! transactions conj {:type type
                            :amount amount
                            :category category
                            :description description
                            :timestamp (java.util.Date.)}))

;; Function to calculate summary
(defn calculate-summary []
  (let [expenses (->> @transactions (filter #(= (:type %) "expense")) (map :amount) (reduce + 0))
        income   (->> @transactions (filter #(= (:type %) "income")) (map :amount) (reduce + 0))]
    {:income income
     :expenses expenses
     :balance (- income expenses)}))

;; GUI for adding a transaction
(defn show-add-transaction []
  (let [type (sc/combobox :model ["income" "expense"])
        amount (sc/text :columns 10)
        category (sc/text :columns 10)
        description (sc/text :columns 20)
        panel (sc/grid-panel :columns 2 :items
                              ["Type:" type
                               "Amount:" amount
                               "Category:" category
                               "Description:" description])]
    (when (= (JOptionPane/showConfirmDialog nil panel "Add Transaction" JOptionPane/OK_CANCEL_OPTION)
             JOptionPane/OK_OPTION)
      (try
        (add-transaction (sc/selection type)
                         (Double/parseDouble (sc/text amount))
                         (sc/text category)
                         (sc/text description))
        (sc/alert "Transaction added successfully!")
        (catch Exception e
          (sc/alert "Invalid input. Please try again!"))))))

;; GUI for listing transactions
(comment (defn show-transactions []
  (let [data (map #(vector (:timestamp %) (:type %) (:category %) (:description %) (:amount %)) @transactions)
        columns ["Date" "Type" "Category" "Description" "Amount"]
        table (sc/table :model [columns data])
        frame (sc/frame :title "Transactions" :content table :width 600 :height 400)]
    (sc/show! frame)
    (sc.show! table))))  ;; Explicitly show the frame
(defn show-transactions []
  ;;(sc/log @transactions)  ;; Log transactions for debugging

  (let [data (map #(vector (:timestamp %) (:type %) (:category %) (:description %) (:amount %)) @transactions)]
   ;; (sc/log data)  ;; Log the mapped data

    (let [columns ["Date" "Type" "Category" "Description" "Amount"]
          table (sc/table :model [columns data])]
      ;;(sc/log table)  ;; Log the table component

      (let [frame (sc/frame :title "Transactions" :content table :width 800 :height 500)]
        (sc/show! table)
        (sc/show! frame)))))


;; GUI for summary
(defn show-summary []
  (let [{:keys [income expenses balance]} (calculate-summary)
        panel (sc/grid-panel :columns 1 :items
                              [(str "Total Income: ksh" income)
                               (str "Total Expenses: ksh" expenses)
                               (str "Net Balance: ksh" balance)])
        frame (sc/frame :title "Summary" :content panel :width 300 :height 250)]
    (sc/show! frame)))  ;; Use a frame instead of sc/alert

;; Main GUI
(defn -main [& args]
  (let [main-frame (sc/frame :title "Finance Manager"
                              :content
                              (sc/vertical-panel
                                :items [(sc/button :text "Add Transaction" :listen [:action (fn [_] (show-add-transaction))])
                                        (sc/button :text "View Transactions" :listen [:action (fn [_] (show-transactions))])
                                        (sc/button :text "View Summary" :listen [:action (fn [_] (show-summary))])])
                              :on-close :exit
                              :width 300 :height 200)]
    (sc/show! main-frame)))

)


(ns finmngr.core
  (:require [seesaw.core :as sc]
            [seesaw.event :as se]
            [cheshire.core :as json])
  (:import [javax.swing JOptionPane])
  (:gen-class))

;; In-memory store for transactions
(def transactions (atom []))

;; Function to validate transaction input
(defn validate-input [type amount category description]
  (cond
    (empty? type) (throw (Exception. "Transaction type is required."))
    (or (nil? amount) (<= amount 0)) (throw (Exception. "Amount must be a positive number."))
    (empty? category) (throw (Exception. "Category is required."))
    (empty? description) (throw (Exception. "Description is required."))
    :else true))

;; Function to add a new transaction
(defn add-transaction [type amount category description]
  (validate-input type amount category description)
  (swap! transactions conj {:type type
                            :amount (double amount) ;; Ensure amount is a Double
                            :category category
                            :description description
                            :timestamp (.toString (java.util.Date.))}))

;; Function to calculate summary
(defn calculate-summary []
  (let [expenses (->> @transactions (filter #(= (:type %) "expense")) (map :amount) (reduce + 0.0))
        income   (->> @transactions (filter #(= (:type %) "income")) (map :amount) (reduce + 0.0))]
    {:income income
     :expenses expenses
     :balance (- income expenses)}))

;; Save transactions to a file
(defn save-transactions []
  (spit "transactions.json" (json/generate-string @transactions)))

;; Load transactions from a file
(defn load-transactions []
  (when (.exists (java.io.File. "transactions.json"))
    (reset! transactions (json/parse-string (slurp "transactions.json") true))))

;; GUI for adding a transaction
(defn show-add-transaction []
  (let [type (sc/combobox :model ["income" "expense"])
        amount (sc/text :columns 10)
        category (sc/text :columns 10)
        description (sc/text :columns 20)
        panel (sc/grid-panel :columns 2 :items
                              ["Type:" type
                               "Amount:" amount
                               "Category:" category
                               "Description:" description])]
    (when (= (JOptionPane/showConfirmDialog nil panel "Add Transaction" JOptionPane/OK_CANCEL_OPTION)
             JOptionPane/OK_OPTION)
      (try
        (add-transaction (sc/selection type)
                         (Double/parseDouble (sc/text amount))
                         (sc/text category)
                         (sc/text description))
        (save-transactions) ;; Save after each transaction
        (sc/alert "Transaction added successfully!")
        (catch Exception e
          (sc/alert (.getMessage e)))))))

;; GUI for listing transactions
(comment(defn show-transactions []
  (let [data (mapv #(vector (:timestamp %) (:type %) (:category %) (:description %) (:amount %))
                   @transactions)
        columns ["Date" "Type" "Category" "Description" "Amount"]
        table (sc/table :model [columns data])]
    (sc/frame :title "Transactions" :content table :width 800 :height 500 :visible? true)))

)

;; GUI for listing transactions in a list view
(defn show-transactions []
  (let [data (map #(str "Date: " (:timestamp %)
                        ", Type: " (:type %)
                        ", Category: " (:category %)
                        ", Description: " (:description %)
                        ", Amount: Ksh " (:amount %))
                  @transactions)
        list-view (sc/listbox :model (vec data))]
    (sc/frame :title "Transactions"
              :content list-view
              :width 1000 :height 400
              :visible? true)))


;; GUI for summary
(defn show-summary []
  (let [{:keys [income expenses balance]} (calculate-summary)
        panel (sc/grid-panel :columns 1 :items
                              [(str "Total Income: Ksh " (format "%.2f" income))
                               (str "Total Expenses: Ksh " (format "%.2f" expenses))
                               (str "Net Balance: Ksh " (format "%.2f" balance))])]
    (sc/frame :title "Summary" :content panel :width 300 :height 250 :visible? true)))

;; Main GUI
(defn -main [& args]
  (load-transactions) ;; Load data on start
  (let [main-frame (sc/frame :title "Finance Manager"
                              :content
                              (sc/vertical-panel
                                :items [(sc/button :text "Add Transaction" :listen [:action (fn [_] (show-add-transaction))])
                                        (sc/button :text "View Transactions" :listen [:action (fn [_] (show-transactions))])
                                        (sc/button :text "View Summary" :listen [:action (fn [_] (show-summary))])])
                              :on-close :exit
                              :width 300 :height 200)]
    (sc/show! main-frame)))


