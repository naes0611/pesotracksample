/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.models;

import java.time.LocalDate;

/**
 *
 * @author lorraineb, seany
 */
public class CategoryModel {
    private long id;
    private Long user_id;
    private String name;
    private LocalDate created_at;

    // ─── Constructors ─────────────────────────────────────────
    
    public CategoryModel(long id, Long user_id, String name, LocalDate created_at) {
        this.id         = id;
        this.user_id    = user_id;
        this.name       = name;
        this.created_at = created_at;
    }
    
    // Constructor for creating a custom category ( not global )
    public CategoryModel(Long user_id, String name) {
        this.user_id   = user_id;
        this.name      = name;
    }
    
    // ─── Getters ──────────────────────────────────────────────
    
    public long getID()              { return id; }
    public Long getUserID()          { return user_id; }
    public String getName()          { return name; }
    public LocalDate getCreatedAt()  { return created_at; }
    
    // Gets global categories
    public boolean isGlobal()        { return user_id == null; }
    
    // ─── Setters ──────────────────────────────────────────────
    
    public void setName(String name)              { this.name = name; }
    public void setCreatedAt(LocalDate created_at) { this.created_at = created_at; }
    
    // ─── toString ─────────────────────────────────────────────
    
    @Override
    public String toString() {
        return name;
    }
}
