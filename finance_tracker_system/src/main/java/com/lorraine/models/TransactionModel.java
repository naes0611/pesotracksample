/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.models;

import com.lorraine.utils.CurrencyFormatter;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author lorraineb, seany
 */
public class TransactionModel {
    
    
    private long id;
    private long user_id;
    private long account_id;
    private long category_id;
    private String category_name;
    private String account_name;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    
    // ─── Constructors ─────────────────────────────────────────
    
    public TransactionModel(long id, long user_id, String account_name, String category_name,
                       TransactionType type, BigDecimal amount,
                       LocalDate date, String description) {
        this.id          = id;
        this.user_id     = user_id;
        this.account_name  = account_name;
        this.category_name = category_name;
        this.type        = type;
        this.amount      = amount;
        this.date        = date;
        this.description = description;
    }
    
    // Constructor for creating a new transaction
    public TransactionModel(long user_id, long account_id, long category_id,
            TransactionType type, BigDecimal amount,
                       LocalDate date, String description) {
        this.user_id     = user_id;
        this.account_id  = account_id;
        this.category_id = category_id;
        this.type        = type;
        this.amount      = amount;
        this.date        = date;
        this.description = description;
    }
    // ─── Getters ──────────────────────────────────────────────

    public long getID()                { return id; }
    public long getUserID()            { return user_id; }
    public long getAccountID()         { return account_id; }
    public long getCategoryID()        { return category_id; }
    public String getAccountName()     { return account_name; }
    public String getCategoryName()    { return category_name; }
    public TransactionType getType()   { return type; }
    public BigDecimal getAmount()      { return amount; }
    public LocalDate getDate()         { return date; }
    public String getDescription()     { return description; }

    // ─── Setters ──────────────────────────────────────────────
    
    public void setCategoryId(long category_id)       { this.category_id = category_id; }
    public void setType(TransactionType type)        { this.type = type; }
    public void setAmount(BigDecimal amount)         { this.amount = amount; }
    public void setDate(LocalDate date)              { this.date = date; }
    public void setDescription(String description)   { this.description = description; }
    
    // ─── toString ─────────────────────────────────────────────

    @Override
    public String toString() {
        return "Transaction{" +
                "id="           + id +
                ", type="       + type +
                ", amount="     + amount +
                ", date="       + date +
                ", description='" + description + '\'' +
                '}';
    }
}
