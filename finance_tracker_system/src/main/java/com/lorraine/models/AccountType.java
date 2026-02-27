/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.lorraine.models;

/**
 *
 * @author lorraineb, seany
 */
public enum AccountType {
    CASH("cash"),
    SAVINGS("savings"),
    CREDIT_CARD("credit_card"),
    OTHER("other");
    
    private final String value;
    
    AccountType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static AccountType fromValue(String value) {
        for (AccountType type : AccountType.values()) {
            if (type.value.equalsIgnoreCase(value)) return type;
        }
        throw new IllegalArgumentException("Unknown account type: " + value);
    }
}