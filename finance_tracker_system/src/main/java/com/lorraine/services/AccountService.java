/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.services;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.lorraine.controllers.DatabaseConnection;
import com.lorraine.models.AccountModel;
import com.lorraine.models.AccountType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author lorraineb, seany
 */
public class AccountService {
    
    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());
    
    // ─── Add Account ──────────────────────────────────────────

    public boolean addAccount(long user_id, String name, AccountType type) {
        String sql = "INSERT INTO accounts (user_id, name, type) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user_id);
            stmt.setString(2, name);
            stmt.setString(3, type.getValue());
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Account added for user_id={0}", user_id);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding account", e);
            return false;
        }
    }

    // ─── Edit Account ─────────────────────────────────────────

    public boolean editAccount(long account_id, String name, AccountType type) {
        String sql = "UPDATE accounts SET name = ?, type = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, type.getValue());
            stmt.setLong(3, account_id);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Account updated id={0}", account_id);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating account", e);
            return false;
        }
    }

    // ─── Delete Account ───────────────────────────────────────

    private static final long SENTINEL_ACCOUNT_ID = 1L;
    
    public boolean deleteAccount(long account_id) {
        String reassignSQL = "UPDATE transactions SET account_id = ? WHERE account_id = ?";
        String deleteSQL   = "DELETE FROM accounts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement reassignStmt = conn.prepareStatement(reassignSQL);
                 PreparedStatement deleteStmt   = conn.prepareStatement(deleteSQL)) {

                reassignStmt.setLong(1, SENTINEL_ACCOUNT_ID);
                reassignStmt.setLong(2, account_id);
                reassignStmt.executeUpdate();

                deleteStmt.setLong(1, account_id);
                deleteStmt.executeUpdate();

                conn.commit();
                LOGGER.log(Level.INFO, "Account deleted id={0}", account_id);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error deleting account, rolled back", e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting account", e);
            return false;
        }
    }

    // ─── Get All Accounts by User ─────────────────────────────

    public List<AccountModel> getAccountsByUser(long user_id) {
        String sql = "SELECT id, user_id, name, type, balance " +
                     "FROM accounts WHERE user_id = ? AND id != 1 ORDER BY name ASC";

        List<AccountModel> accounts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching accounts", e);
        }
        return accounts;
    }

    // ─── Private Helper ───────────────────────────────────────

    private AccountModel mapRow(ResultSet rs) throws SQLException {
        return new AccountModel(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("name"),
            AccountType.fromValue(rs.getString("type")),
            rs.getBigDecimal("balance")
        );
    }
    
}
