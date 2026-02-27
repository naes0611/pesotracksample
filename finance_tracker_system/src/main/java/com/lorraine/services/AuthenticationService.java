/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.services;

import com.lorraine.models.UserModel;
import com.lorraine.controllers.DatabaseConnection;
import com.lorraine.models.AuthenticationResult;
import com.lorraine.models.LoginResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author lorraineb, seany
 */
public class AuthenticationService {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    
    // ─── Register ─────────────────────────────────────────────
    
    public AuthenticationResult registerUser(String first_name, String last_name, String email, String password) {
        
        if (emailExists(email)) {
            LOGGER.log(Level.WARNING, "Registration failed: Email already exists - {0}", email);
            return AuthenticationResult.failed("Email already exists");
        }
        
        String hashedPassword = hashPassword(password);
        if (insertUser(first_name, last_name, email, hashedPassword)) {
            LOGGER.log(Level.INFO, "User registered successfully - {0}", email);
            return AuthenticationResult.success("User registered successfully");
        } else {
            return AuthenticationResult.failed("Registration failed due to database error");
        }
    }
    
    // ─── Login ─────────────────────────────────────────────
    
    public LoginResult loginUser(String email, String password) {
        UserModel user = getUserByEmail(email);
        
        if (user == null) {
            LOGGER.log(Level.WARNING, "Login failed: User not found - {0}", email);
            return LoginResult.failed("Invalid email or password");
        }
        
        if (verifyPassword(password, user.getPassword())) {
            LOGGER.log(Level.INFO, "User logged in successfully - {0}", email);
            return LoginResult.success("Login successful", user);
        } else {
            LOGGER.log(Level.WARNING, "Login failed: Incorrect password - {0}", email);
            return LoginResult.failed("Invalid email or password");
        }
    }
    
    // ─── Get UserModel From Database ─────────────────────────────────────────────
    
    public UserModel getUserByEmail(String email) {
        String query = "SELECT user_id, first_name, last_name, email, password, created_at FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserModel(
                        rs.getLong("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getDate("created_at").toLocalDate() 
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user from database", e);
        }
        return null;
    }
    
    // ─── Helpers ─────────────────────────────────────────────
    
    private boolean emailExists(String email) {
        String query = "SELECT 1 FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists", e);
            return true;
        }
        return false;
    }
    
    private boolean insertUser(String first_name, String last_name, String email, String hashedPassword) {
        String query = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, first_name);
            stmt.setString(2, last_name);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting user into database", e);
            return false;
        }
    }
    
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
