/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.models;

import java.math.BigDecimal;

/**
 *
 * @author lorraineb, seany
 */
public class AccountModel {
    private long id;
    private long user_id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    
    // ─── Constructors ─────────────────────────────────────────
    
    public AccountModel(long id, long user_id, String name, AccountType type, BigDecimal balance) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }
    
    // Constructor for creating a new account
    public AccountModel(long user_id, String name, AccountType type, BigDecimal balance) {
        this.user_id = user_id;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }
    
    // ─── Getters ─────────────────────────────────────────
    
    public long getID()            { return id; }
    public long getUserID()        { return user_id; }
    public String getName()        { return name; }
    public AccountType getType()   { return type; }
    public BigDecimal getBalance() { return balance; }
    
    // ─── Setters ─────────────────────────────────────────
    
    public void setName(String name)              { this.name = name; }
    public void setType(AccountType type)         { this.type = type; }
    public void setBalance(BigDecimal balance)    { this.balance = balance; }
    
    // ─── toString ─────────────────────────────────────────
    
    @Override
    public String toString() {
        return name;
    }
}
