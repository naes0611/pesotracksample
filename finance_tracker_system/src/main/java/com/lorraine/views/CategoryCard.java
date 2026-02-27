/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.views;

import com.formdev.flatlaf.FlatClientProperties;
import com.lorraine.models.CategoryModel;
import javax.swing.JLabel;
import com.lorraine.swing.ButtonAction;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
/**
 *
 * @author lorraineb, seany
 */
public class CategoryCard extends JPanel {
    
    private final CategoryModel category;
    private final boolean isGlobal;
    private Consumer<CategoryModel> onEdit;
    private Consumer<CategoryModel> onDelete;
    
    public CategoryCard(CategoryModel category, boolean isGlobal) {
        this.category = category;
        this.isGlobal = isGlobal;
        initComponents();
        applyStyle();
        populate();
        setPreferredSize(new Dimension(210, isGlobal ? 60 : 95));
    }
    
    public void setOnEdit(Consumer<CategoryModel> onEdit) { 
        this.onEdit   = onEdit;
    }
    public void setOnDelete(Consumer<CategoryModel> onDelete) { 
        this.onDelete = onDelete;
    }
    
    private void populate() {
        nameLabel.setText(category.getName());

        if (isGlobal) {
            typeBadge.setText("Global");
            typeBadge.setBackground(new Color(243, 244, 246));
            typeBadge.setForeground(new Color(156, 163, 175));
            editButton.setVisible(false);
            deleteButton.setVisible(false);
        } else {
            typeBadge.setText("Custom");
            typeBadge.setBackground(new Color(239, 246, 255));
            typeBadge.setForeground(new Color(59, 130, 246));
        }
    }
    
    private void applyStyle() {
        putClientProperty(FlatClientProperties.STYLE, "arc:16; background:#ffffff;");
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(232, 234, 240), 1),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        typeBadge.setOpaque(true);
        typeBadge.putClientProperty(FlatClientProperties.STYLE, "arc:99;");
        typeBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        typeBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));

        nameLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        nameLabel.setForeground(new Color(17, 24, 39));

        editButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:8; borderWidth:0; focusWidth:0; innerFocusWidth:0; background:#f0f9ff; foreground:#0369a1;");
        deleteButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:8; borderWidth:0; focusWidth:0; innerFocusWidth:0; background:#fff1f2; foreground:#be123c;");
    }

    
    private void initComponents() {
        nameLabel = new JLabel();
        typeBadge = new JLabel();
        editButton = new ButtonAction();
        deleteButton = new ButtonAction();
        
        editButton.setText("Edit");
        deleteButton.setText("Delete");
        
        editButton.addActionListener(e -> { if (onEdit != null) onEdit.accept(category); });
        deleteButton.addActionListener(e -> { if (onDelete != null) onDelete.accept(category); });
        
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8)
                        .addComponent(typeBadge))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(editButton,   GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                        .addGap(8)
                        .addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(16))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(14)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(nameLabel)
                    .addComponent(typeBadge))
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(editButton,   GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(14)
        );
    }
    
    private JLabel nameLabel;
    private JLabel typeBadge;
    private ButtonAction editButton;
    private ButtonAction deleteButton;
}
