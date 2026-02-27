/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.views;

import com.formdev.flatlaf.FlatClientProperties;
import com.lorraine.models.AccountModel;
import com.lorraine.swing.ButtonAction;
import com.lorraine.utils.CurrencyFormatter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
/**
 *
 * @author lorraineb, seany
 */
public class AccountCard extends JPanel {
    
    private final AccountModel account;
    private Consumer<AccountModel> onEdit;
    private Consumer<AccountModel> onDelete;
    
    public AccountCard(AccountModel account) {
        this.account = account;
        initComponents();
        applyStyle();
        populate();
        setPreferredSize(new Dimension(210, 155));
    }
    
    public void setOnEdit(Consumer<AccountModel> onEdit)     { this.onEdit   = onEdit; }
    public void setOnDelete(Consumer<AccountModel> onDelete) { this.onDelete = onDelete; }
    
    private void populate() {
        nameLabel.setText(account.getName());
        balanceLabel.setText(CurrencyFormatter.format(account.getBalance()));
        typeBadge.setText(formatType(account.getType().getValue()));
        typeBadge.setBackground(badgeBackground(account.getType().getValue()));
        typeBadge.setForeground(badgeForeground(account.getType().getValue()));
    }
    
    private String formatType(String type) {
        return switch (type) {
            case "cash"        -> "Cash";
            case "savings"     -> "Savings";
            case "credit_card" -> "Credit Card";
            default            -> "Other";
        };
    }
    
    private Color badgeBackground(String type) {
        return switch (type) {
            case "cash"        -> new Color(220, 252, 231);
            case "savings"     -> new Color(219, 234, 254);
            case "credit_card" -> new Color(252, 231, 243);
            default            -> new Color(243, 244, 246);
        };
    }
    
    private Color badgeForeground(String type) {
        return switch (type) {
            case "cash"        -> new Color(21,  128, 61);
            case "savings"     -> new Color(29,  78,  216);
            case "credit_card" -> new Color(190, 24,  93);
            default            -> new Color(107, 114, 128);
        };
    }
    
    private void applyStyle() {
        putClientProperty(FlatClientProperties.STYLE, "arc:16; background:#ffffff;");
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(232, 234, 240), 1),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        typeBadge.setOpaque(true);
        typeBadge.putClientProperty(FlatClientProperties.STYLE, "arc:99;");
        typeBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        typeBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));

        nameLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        nameLabel.setForeground(new Color(17, 24, 39));

        balanceLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 20));
        balanceLabel.setForeground(new Color(17, 24, 39));

        editButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:8; borderWidth:0; focusWidth:0; innerFocusWidth:0; background:#f0f9ff; foreground:#0369a1;");
        deleteButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:8; borderWidth:0; focusWidth:0; innerFocusWidth:0; background:#fff1f2; foreground:#be123c;");
    }
    
    private void initComponents() {
        typeBadge = new JLabel();
        nameLabel = new JLabel();
        balanceLabel = new JLabel();
        editButton   = new ButtonAction();
        deleteButton = new ButtonAction();
        
        editButton.setText("Edit");
        deleteButton.setText("Delete");
        
        editButton.addActionListener(e   -> { if (onEdit   != null) onEdit.accept(account); });
        deleteButton.addActionListener(e -> { if (onDelete != null) onDelete.accept(account); });
        
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(typeBadge)
                    .addComponent(nameLabel)
                    .addComponent(balanceLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(editButton,   GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                        .addGap(8)
                        .addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
                .addGap(16))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(16)
                .addComponent(typeBadge)
                .addGap(8)
                .addComponent(nameLabel)
                .addGap(4)
                .addComponent(balanceLabel)
                .addGap(12)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(editButton,   GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(16)
        );
    }
    
    private JLabel typeBadge;
    private JLabel nameLabel;
    private JLabel balanceLabel;
    private ButtonAction editButton;
    private ButtonAction deleteButton;
}
