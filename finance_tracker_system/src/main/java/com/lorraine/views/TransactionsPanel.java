/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.lorraine.views;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lorraine.main.MainFrame;
import com.lorraine.main.Session;
import com.lorraine.models.AccountModel;
import com.lorraine.models.CategoryModel;
import com.lorraine.models.TransactionModel;
import com.lorraine.models.TransactionType;
import com.lorraine.services.AccountService;
import com.lorraine.services.CategoryService;
import com.lorraine.services.TransactionService;
import com.lorraine.swing.CheckBoxTableHeaderRenderer;
import com.lorraine.swing.TableHeaderAlignment;
import com.lorraine.utils.CurrencyFormatter;
import com.lorraine.utils.DataLoader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentListener;
import raven.datetime.DatePicker;
import raven.popup.component.PopupController;
/**
 *
 * @author lorraineb, seany
 */
public class TransactionsPanel extends javax.swing.JPanel {
    
    private static final Logger LOGGER = Logger.getLogger(TransactionsPanel.class.getName());
    
    private DatePicker datePicker;
    private final List<TransactionModel> loadedTransactions = new ArrayList<>();
    
    private final TransactionService transactionService = new TransactionService();
    private final CategoryService categoryService = new CategoryService();
    private final AccountService accountService = new AccountService();
    /**
     * Creates new form TransactionPanel
     */
    public TransactionsPanel() {
        initComponents();
        initDatePicker();
        initComponentProperties();
        initSearchControls();
        loadTransactions();
    }
    
    private void initComponentProperties() {
        String roundedStyle = "arc:15; borderWidth:1; focusWidth:0; innerFocusWidth:0;";    
        
        typesFilterCBX.putClientProperty(FlatClientProperties.STYLE, roundedStyle);
        datePickerField.putClientProperty(FlatClientProperties.STYLE, roundedStyle);
        accountsFilterCBX.putClientProperty(FlatClientProperties.STYLE, roundedStyle);
        categoriesFilterCBX.putClientProperty(FlatClientProperties.STYLE, roundedStyle);
        
        tableContainer.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        
        transactionTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");
        
        transactionTable.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:30;"
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");
        
        tableScroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
        
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search Description");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/search.svg"));
        searchField.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "background:$Panel.background");
        
        transactionTable.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(transactionTable, 0));
        transactionTable.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(transactionTable));
    }
    
    private void initDatePicker() {
        datePicker = new DatePicker();
        datePicker.setEditor(datePickerField);
        datePicker.setCloseAfterSelected(true);
        datePicker.setDateSelectionAble(date -> !date.isAfter(LocalDate.now()));
    }
    
    private void initSearchControls() {
        DataLoader.loadAccounts(accountService, Session.getUserID(), accountsFilterCBX);
        DataLoader.loadCategories(categoryService, Session.getUserID(), categoriesFilterCBX);
        DataLoader.loadTransactionTypes(typesFilterCBX);

        DocumentListener onType = new DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { performSearch(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { performSearch(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
        };
        
        searchField.getDocument().addDocumentListener(onType);
        accountsFilterCBX.addActionListener(e -> performSearch());
        categoriesFilterCBX.addActionListener(e -> performSearch());
        typesFilterCBX.addActionListener(e -> performSearch());
        datePicker.addDateSelectionListener(e -> performSearch());
    }
    
    private void performSearch() {
        String description = searchField.getText().trim();
        
        String accountName  = accountsFilterCBX.getSelectedIndex()   == 0 ? null : ((AccountModel)   accountsFilterCBX.getSelectedItem()).getName();
        String categoryName = categoriesFilterCBX.getSelectedIndex() == 0 ? null : ((CategoryModel)  categoriesFilterCBX.getSelectedItem()).getName();
        TransactionType type = typesFilterCBX.getSelectedIndex()     == 0 ? null : (TransactionType)  typesFilterCBX.getSelectedItem();
        LocalDate date  = datePicker.isDateSelected() ? datePicker.getSelectedDate() : null;
         
        populateTable(transactionService.searchTransactions(
            Session.getUserID(), description, 
            accountName,
            categoryName,
            type, date
        ));
    }
    
    private void loadTransactions() {
        populateTable(transactionService.getTransactionsByUser(Session.getUserID()));
    }
    
    private void populateTable(List<TransactionModel> transactions) {
        try {
            DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
            if (transactionTable.isEditing()) transactionTable.getCellEditor().stopCellEditing();
            model.setRowCount(0);
            loadedTransactions.clear();
            for (TransactionModel t : transactions) {
                model.addRow(new Object[]{
                    false,
                    t.getDate(),
                    t.getDescription(),
                    t.getCategoryName(),
                    t.getAccountName(),
                    t.getType().getDisplayValue(),
                    CurrencyFormatter.format(t.getAmount())
                });
                loadedTransactions.add(t);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error populating table", e);
        }
    }
    
    private List<TransactionModel> getSelectedData() {
        List<TransactionModel> selected = new ArrayList<>();
        for (int i = 0; i < transactionTable.getRowCount(); i++) {
            boolean isChecked = (boolean) transactionTable.getValueAt(i, 0);
            if (isChecked) {
                selected.add(loadedTransactions.get(i));
            }
        }
        return selected;
    }
    
    private void showTransactionPopup(String title, String confirmLabel,
                                       TransactionForm form,
                                       TransactionPopupAction onConfirm) {
        DefaultOption option = new DefaultOption() {
            @Override public boolean closeWhenClickOutside() { return true; }
        };

        GlassPanePopup.showPopup(new SimplePopupBorder(form, title,
            new String[]{confirmLabel, "Cancel"}, (pc, i) -> {
                if (i != 0) { pc.closePopup(); return; }
                
                TransactionModel data = form.getData();
                if (data == null) {
                    Notifications.getInstance().show(Notifications.Type.WARNING, "Please fill in all fields.");
                    return;
                }
                
                onConfirm.execute(pc, data);
            }), option);
    }

    @FunctionalInterface
    private interface TransactionPopupAction {
        void execute(PopupController pc, TransactionModel data);
    }

    private TransactionForm buildForm() {
        TransactionForm form = new TransactionForm();
        DataLoader.loadAccountsForForm(accountService, Session.getUserID(), form.getAccountCBX());
        DataLoader.loadCategoriesForForm(categoryService, Session.getUserID(), form.getCategoryCBX());
        DataLoader.loadTransactionTypesForForm(form.getTypeCBX());
        return form;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        transactionContainer = new javax.swing.JPanel();
        tableContainer = new javax.swing.JPanel();
        tableScroll = new javax.swing.JScrollPane();
        transactionTable = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        searchField = new javax.swing.JTextField();
        addButton = new com.lorraine.swing.ButtonAction();
        editButton = new com.lorraine.swing.ButtonAction();
        deleteButton = new com.lorraine.swing.ButtonAction();
        categoriesFilterCBX = new javax.swing.JComboBox<>();
        accountsFilterCBX = new javax.swing.JComboBox<>();
        typesFilterCBX = new javax.swing.JComboBox<>();
        datePickerField = new javax.swing.JFormattedTextField();

        setPreferredSize(new java.awt.Dimension(900, 600));

        transactionContainer.setPreferredSize(new java.awt.Dimension(900, 600));

        tableContainer.setPreferredSize(new java.awt.Dimension(864, 564));

        tableScroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        transactionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SELECT", "DATE", "DESCRIPTION", "CATEGORY", "ACCOUNT", "TYPE", "AMOUNT"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        transactionTable.setPreferredSize(null);
        transactionTable.getTableHeader().setReorderingAllowed(false);
        tableScroll.setViewportView(transactionTable);
        if (transactionTable.getColumnModel().getColumnCount() > 0) {
            transactionTable.getColumnModel().getColumn(0).setMaxWidth(50);
            transactionTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            transactionTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            transactionTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            transactionTable.getColumnModel().getColumn(4).setPreferredWidth(100);
            transactionTable.getColumnModel().getColumn(5).setPreferredWidth(100);
            transactionTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        addButton.setText("Add Transaction");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        categoriesFilterCBX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoriesFilterCBXActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tableContainerLayout = new javax.swing.GroupLayout(tableContainer);
        tableContainer.setLayout(tableContainerLayout);
        tableContainerLayout.setHorizontalGroup(
            tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(tableContainerLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tableContainerLayout.createSequentialGroup()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tableContainerLayout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(categoriesFilterCBX, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(accountsFilterCBX, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(typesFilterCBX, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(datePickerField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(tableContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScroll)
                .addContainerGap())
        );
        tableContainerLayout.setVerticalGroup(
            tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tableContainerLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoriesFilterCBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accountsFilterCBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typesFilterCBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datePickerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout transactionContainerLayout = new javax.swing.GroupLayout(transactionContainer);
        transactionContainer.setLayout(transactionContainerLayout);
        transactionContainerLayout.setHorizontalGroup(
            transactionContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transactionContainerLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(tableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );
        transactionContainerLayout.setVerticalGroup(
            transactionContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transactionContainerLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(tableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(transactionContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(transactionContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
//        TransactionForm form = new TransactionForm();
//        DataLoader.loadAccountsForForm(accountService, currentUserID, form.getAccountCBX());
//        DataLoader.loadCategoriesForForm(categoryService, currentUserID, form.getCategoryCBX());
//        DataLoader.loadTransactionTypesForForm(form.getTypeCBX());
//        
//        DefaultOption option = new DefaultOption() {
//            @Override public boolean closeWhenClickOutside() { return true; }
//        };
//        
//        GlassPanePopup.showPopup(new SimplePopupBorder(form, "Add Transaction", 
//            new String[]{"Add Transaction", "Cancel"}, (pc, i) -> {
//            if (i != 0) { 
//                pc.closePopup();
//                return;
//            }
//            
//            TransactionModel data = form.getData();
//            if (data == null) {
//                Notifications.getInstance().show(Notifications.Type.WARNING, "Please fill in all fields.");
//                return;
//            }
//
//            boolean success = transactionService.addTransaction(
//                data.getUserID(), data.getAccountID(), data.getCategoryID(),
//                data.getType(), data.getAmount(), data.getDate(), data.getDescription()
//            );
//
//            if (success) {
//                pc.closePopup();
//                loadTransactions();
//                Notifications.getInstance().show(Notifications.Type.SUCCESS, "Transaction added successfully.");
//            } else {
//                Notifications.getInstance().show(Notifications.Type.ERROR, "Failed to add transaction.");
//            }
//        }), option);


        TransactionForm form = buildForm();
        
        showTransactionPopup("Add Transaction", "Add Transaction", form, (pc, data) -> {
            boolean success = transactionService.addTransaction(
                data.getUserID(), data.getAccountID(), data.getCategoryID(),
                data.getType(), data.getAmount(), data.getDate(), data.getDescription()
            );
            if (success) {
                pc.closePopup();
                loadTransactions();
                Notifications.getInstance().show(Notifications.Type.SUCCESS, "Transaction added successfully.");
            } else {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Failed to add transaction.");
            }
        });
        
    }//GEN-LAST:event_addButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
//        List<TransactionModel> selected = getSelectedData();
//        
//        if (selected.isEmpty()) {
//            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select a transaction to edit.");
//            return;
//        }
//        if (selected.size() > 1) {
//            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select only one transaction to edit.");
//            return;
//        }
//        
//        TransactionModel existing = selected.get(0);
//        
//        TransactionForm form = new TransactionForm();
//        DataLoader.loadAccountsForForm(accountService, currentUserID, form.getAccountCBX());
//        DataLoader.loadCategoriesForForm(categoryService, currentUserID, form.getCategoryCBX());
//        form.prefill(existing);
//        
//        DefaultOption option = new DefaultOption() {
//            @Override public boolean closeWhenClickOutside() { return true; }
//        };
//        
//        GlassPanePopup.showPopup(new SimplePopupBorder(form, "Edit Transaction",
//            new String[]{"Save Changes", "Cancel"}, (pc, i) -> {
//                if (i != 0) { pc.closePopup(); return; }
//
//                TransactionModel data = form.getData();
//                if (data == null) {
//                    Notifications.getInstance().show(Notifications.Type.WARNING, "Please fill in all fields.");
//                    return;
//                }
//
//                boolean success = transactionService.editTransaction(
//                    existing.getID(), data.getAccountID(), data.getCategoryID(),
//                    data.getType(), data.getAmount(), data.getDate(), data.getDescription()
//                );
//
//                if (success) {
//                    pc.closePopup();
//                    loadTransactions();
//                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Transaction updated successfully.");
//                } else {
//                    Notifications.getInstance().show(Notifications.Type.ERROR, "Failed to update transaction.");
//                }
//            }), option);

        List<TransactionModel> selected = getSelectedData();
        
        if (selected.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select a transaction to edit.");
            return;
        }
        if (selected.size() > 1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select only one transaction to edit.");
            return;
        }

        TransactionModel existing = selected.get(0);
        TransactionForm form = buildForm();
        form.prefill(existing);
        
        showTransactionPopup("Edit Transaction", "Save Changes", form, (pc, data) -> {
            boolean success = transactionService.editTransaction(
                existing.getID(), data.getAccountID(), data.getCategoryID(),
                data.getType(), data.getAmount(), data.getDate(), data.getDescription()
            );
            if (success) {
                pc.closePopup();
                loadTransactions();
                Notifications.getInstance().show(Notifications.Type.SUCCESS, "Transaction updated successfully.");
            } else {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Failed to update transaction.");
            }
        });
    }//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
//        List<TransactionModel> selected = getSelectedData();
//
//        if (selected.isEmpty()) {
//            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select a transaction to delete.");
//            return;
//        }
//
//        // TODO: show confirmation popup before deleting
//        for (TransactionModel t : selected) {
//            transactionService.deleteTransaction(t.getID());
//        }
//
//        loadTransactions();
//        Notifications.getInstance().show(Notifications.Type.SUCCESS,
//        selected.size() + " transaction(s) deleted.");

//        List<TransactionModel> selected = getSelectedData();
//
//        if (selected.isEmpty()) {
//            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select a transaction to delete.");
//            return;
//        }
//
//        // TODO: add confirmation popup before deleting
//        selected.forEach(t -> transactionService.deleteTransaction(t.getID()));
//        loadTransactions();
//        Notifications.getInstance().show(Notifications.Type.SUCCESS,
//            selected.size() + " transaction(s) deleted.");
    
        System.out.println(transactionService.getTransactionsByUser(Session.getUserID()));
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void categoriesFilterCBXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoriesFilterCBXActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoriesFilterCBXActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Object> accountsFilterCBX;
    private com.lorraine.swing.ButtonAction addButton;
    private javax.swing.JComboBox<Object> categoriesFilterCBX;
    private javax.swing.JFormattedTextField datePickerField;
    private com.lorraine.swing.ButtonAction deleteButton;
    private com.lorraine.swing.ButtonAction editButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel tableContainer;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JPanel transactionContainer;
    private javax.swing.JTable transactionTable;
    private javax.swing.JComboBox<Object> typesFilterCBX;
    // End of variables declaration//GEN-END:variables
}
