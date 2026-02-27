/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.lorraine.views;

import com.formdev.flatlaf.FlatClientProperties;
import com.lorraine.main.Session;
import com.lorraine.models.CategoryModel;
import com.lorraine.services.CategoryService;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.PopupController;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;

/**
 *
 * @author seany
 */
public class CategoriesPanel extends javax.swing.JPanel {
    
    private static final Logger LOGGER = Logger.getLogger(CategoriesPanel.class.getName());
    private final CategoryService categoryService = new CategoryService();
    
    /**
     * Creates new form CategoriesPanel
     */
    public CategoriesPanel() {
        initComponents();
        initComponentProperties();
        loadCategories();
    }

    private void initComponentProperties() {
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc:999; trackInsets:3,3,3,3; thumbInsets:3,3,3,3;");
        
        scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        addButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; borderWidth:0; focusWidth:0; innerFocusWidth:0;");

        filterCBX.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; borderWidth:1; focusWidth:0; innerFocusWidth:0;");

        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 20));
        titleLabel.setForeground(new Color(17, 24, 39));

        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        filterLabel.setForeground(new Color(107, 114, 128));

        applySummaryStyle(totalCard,  new Color(243, 244, 246), new Color(55,  65,  81));
        applySummaryStyle(globalCard, new Color(226, 232, 240), new Color(51,  65,  85));
        applySummaryStyle(customCard, new Color(224, 231, 255), new Color(55,  48,  163));
    }

    private void applySummaryStyle(JPanel card, Color background, Color foreground) {
        card.setBackground(background);
        card.putClientProperty(FlatClientProperties.STYLE, "arc:14;");

        JLabel label = null, value = null;
        for (int i = 0; i < card.getComponentCount(); i++) {
            if (card.getComponent(i) instanceof JLabel lbl) {
                if (label == null) label = lbl;
                else value = lbl;
            }   
        }
        
        if (label == null || value == null) return;

        if (label.getFont().getSize() > value.getFont().getSize()) {
            JLabel tmp = label; label = value; value = tmp;
        }

        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(foreground.getRed(), foreground.getGreen(), foreground.getBlue(), 180));

        value.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
        value.setForeground(foreground);
    }
    
    private JLabel getValueLabel(JPanel card) {
        for (int i = 0; i < card.getComponentCount(); i++) {
            if (card.getComponent(i) instanceof JLabel lbl && lbl.getFont().getSize() > 15) {
                return lbl;
            }
        }
        return (JLabel) card.getComponent(1);
    }
    
    private void loadCategories() {
        cardsContainer.removeAll();

        try {
            List<CategoryModel> categories = categoryService.getCategoriesByUser(Session.getUserID());

            String filter    = (String) filterCBX.getSelectedItem();
            long globalCount = categories.stream().filter(c -> c.getUserID() == null).count();
            long customCount = categories.size() - globalCount;

            getValueLabel(totalCard).setText(String.valueOf(categories.size()));
            getValueLabel(globalCard).setText(String.valueOf(globalCount));
            getValueLabel(customCard).setText(String.valueOf(customCount));

            for (CategoryModel category : categories) {
                boolean isGlobal = category.getUserID() == null;

                if ("Global".equals(filter) && !isGlobal) continue;
                if ("Custom".equals(filter) &&  isGlobal) continue;

                CategoryCard card = new CategoryCard(category, isGlobal);
                if (!isGlobal) {
                    card.setOnEdit(this::onEditCategory);
                    card.setOnDelete(this::onDeleteCategory);
                }
                cardsContainer.add(card);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading categories", e);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    private void showCategoryPopup(String title, String confirmLabel,
                                       CategoryForm form,
                                       CategoryPopupAction onConfirm) {
        DefaultOption option = new DefaultOption() {
            @Override public boolean closeWhenClickOutside() { return true; }
        };

        GlassPanePopup.showPopup(new SimplePopupBorder(form, title,
            new String[]{confirmLabel, "Cancel"}, (pc, i) -> {
                if (i != 0) { pc.closePopup(); return; }
                
                CategoryModel data = form.getData();
                if (data == null) {
                    Notifications.getInstance().show(Notifications.Type.WARNING, "Please fill in all fields.");
                    return;
                }
                
                onConfirm.execute(pc, data);
            }), option);
    }

    @FunctionalInterface
    private interface CategoryPopupAction {
        void execute(PopupController pc, CategoryModel data);
    }

    private CategoryForm buildForm() {
        CategoryForm form = new CategoryForm();
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

        mainContainer = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        addButton = new com.lorraine.swing.ButtonAction();
        summaryPanel = new javax.swing.JPanel();
        totalCard = new javax.swing.JPanel();
        totalLabel = new javax.swing.JLabel();
        totalValue = new javax.swing.JLabel();
        globalCard = new javax.swing.JPanel();
        globalLabel = new javax.swing.JLabel();
        globalValue = new javax.swing.JLabel();
        customCard = new javax.swing.JPanel();
        customLabel = new javax.swing.JLabel();
        customValue = new javax.swing.JLabel();
        filterPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterCBX = new javax.swing.JComboBox<>();
        scrollPane = new javax.swing.JScrollPane();
        cardsContainer = new com.lorraine.swing.CardsPanel();

        mainContainer.setPreferredSize(new java.awt.Dimension(900, 600));

        headerPanel.setPreferredSize(new java.awt.Dimension(852, 28));

        titleLabel.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        titleLabel.setText("Categories");

        addButton.setText("Add Category");
        addButton.setToolTipText("");
        addButton.setPreferredSize(new java.awt.Dimension(120, 28));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(titleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        summaryPanel.setPreferredSize(new java.awt.Dimension(852, 80));

        totalCard.setPreferredSize(new java.awt.Dimension(272, 80));

        totalLabel.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        totalLabel.setText("TOTAL");

        totalValue.setFont(new java.awt.Font("Segoe UI Semibold", 0, 22)); // NOI18N
        totalValue.setText("0");

        javax.swing.GroupLayout totalCardLayout = new javax.swing.GroupLayout(totalCard);
        totalCard.setLayout(totalCardLayout);
        totalCardLayout.setHorizontalGroup(
            totalCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalCardLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(totalCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalValue)
                    .addComponent(totalLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        totalCardLayout.setVerticalGroup(
            totalCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(totalLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalValue)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        globalCard.setPreferredSize(new java.awt.Dimension(272, 80));

        globalLabel.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        globalLabel.setText("GLOBAL");

        globalValue.setFont(new java.awt.Font("Segoe UI Semibold", 0, 22)); // NOI18N
        globalValue.setText("0");

        javax.swing.GroupLayout globalCardLayout = new javax.swing.GroupLayout(globalCard);
        globalCard.setLayout(globalCardLayout);
        globalCardLayout.setHorizontalGroup(
            globalCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(globalCardLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(globalCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(globalLabel)
                    .addComponent(globalValue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        globalCardLayout.setVerticalGroup(
            globalCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(globalCardLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(globalLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(globalValue)
                .addGap(14, 14, 14))
        );

        customCard.setPreferredSize(new java.awt.Dimension(272, 80));

        customLabel.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        customLabel.setText("CUSTOM");

        customValue.setFont(new java.awt.Font("Segoe UI Semibold", 0, 22)); // NOI18N
        customValue.setText("0");

        javax.swing.GroupLayout customCardLayout = new javax.swing.GroupLayout(customCard);
        customCard.setLayout(customCardLayout);
        customCardLayout.setHorizontalGroup(
            customCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customCardLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(customCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customValue)
                    .addComponent(customLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        customCardLayout.setVerticalGroup(
            customCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(customLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customValue)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout summaryPanelLayout = new javax.swing.GroupLayout(summaryPanel);
        summaryPanel.setLayout(summaryPanelLayout);
        summaryPanelLayout.setHorizontalGroup(
            summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(summaryPanelLayout.createSequentialGroup()
                .addComponent(totalCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(globalCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(customCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        summaryPanelLayout.setVerticalGroup(
            summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(summaryPanelLayout.createSequentialGroup()
                .addGroup(summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(customCard, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(globalCard, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        filterPanel.setPreferredSize(new java.awt.Dimension(864, 28));

        filterLabel.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        filterLabel.setForeground(new java.awt.Color(102, 102, 102));
        filterLabel.setText("SHOW");
        filterLabel.setPreferredSize(new java.awt.Dimension(43, 26));

        filterCBX.setMaximumRowCount(3);
        filterCBX.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Global", "Custom" }));
        filterCBX.setPreferredSize(new java.awt.Dimension(120, 30));
        filterCBX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCBXActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout filterPanelLayout = new javax.swing.GroupLayout(filterPanel);
        filterPanel.setLayout(filterPanelLayout);
        filterPanelLayout.setHorizontalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(filterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterCBX, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        filterPanelLayout.setVerticalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addComponent(filterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(filterCBX, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new java.awt.Dimension(852, 360));

        cardsContainer.setBackground(new java.awt.Color(0, 0, 0));
        scrollPane.setViewportView(cardsContainer);

        javax.swing.GroupLayout mainContainerLayout = new javax.swing.GroupLayout(mainContainer);
        mainContainer.setLayout(mainContainerLayout);
        mainContainerLayout.setHorizontalGroup(
            mainContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainerLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(mainContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
                    .addComponent(summaryPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );
        mainContainerLayout.setVerticalGroup(
            mainContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContainerLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(summaryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        CategoryForm form = buildForm();
        showCategoryPopup("Add Category", "Add Category", form, (pc, data) -> {
            boolean success = categoryService.addCategory(
                    Session.getUserID(), data.getName()
            );
            if (success) {
                pc.closePopup();
                loadCategories();
                Notifications.getInstance().show(Notifications.Type.SUCCESS, "Category added successfully.");
            } else {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Failed to add category.");
            }
        });
    }//GEN-LAST:event_addButtonActionPerformed

    private void onEditCategory(CategoryModel category) {
        CategoryForm form = buildForm();
        form.prefill(category);
        showCategoryPopup("Edit Category", "Save Changes", form, (pc, data) -> {
           boolean success = categoryService.editCategory(
                   category.getID(), data.getName()
           );
           if (success) {
               pc.closePopup();
               loadCategories();
               Notifications.getInstance().show(Notifications.Type.SUCCESS, "Category edited successfully.");
            } else {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Failed to edit category.");
            }
        });
    }

    private void onDeleteCategory(CategoryModel category) {
        boolean success = categoryService.deleteCategory(category.getID());
        if (success) {
            loadCategories();
            Notifications.getInstance().show(raven.toast.Notifications.Type.SUCCESS, "Category deleted.");
        } else {
            Notifications.getInstance().show(raven.toast.Notifications.Type.ERROR, "Failed to delete category.");
        }
    }
    
    private void filterCBXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCBXActionPerformed
        loadCategories();
    }//GEN-LAST:event_filterCBXActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.lorraine.swing.ButtonAction addButton;
    private com.lorraine.swing.CardsPanel cardsContainer;
    private javax.swing.JPanel customCard;
    private javax.swing.JLabel customLabel;
    private javax.swing.JLabel customValue;
    private javax.swing.JComboBox<String> filterCBX;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel globalCard;
    private javax.swing.JLabel globalLabel;
    private javax.swing.JLabel globalValue;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel mainContainer;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel totalCard;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JLabel totalValue;
    // End of variables declaration//GEN-END:variables
}
