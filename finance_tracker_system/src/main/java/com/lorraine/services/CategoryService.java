/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.services;

import com.lorraine.controllers.DatabaseConnection;
import com.lorraine.models.CategoryModel;
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
public class CategoryService {

    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());

    // ─── Add CategoryModel ─────────────────────────────────────────

    public boolean addCategory(long user_id, String name) {
        String sql = "INSERT INTO categories (user_id, name) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user_id);
            stmt.setString(2, name);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Category added for user_id={0}", user_id);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding category", e);
            return false;
        }
    }

    // ─── Edit CategoryModel ────────────────────────────────────────

    public boolean editCategory(long category_id, String name) {
        String sql = "UPDATE categories SET name = ? " +
                     "WHERE id = ? AND user_id IS NOT NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setLong(2, category_id);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Category updated id={0}", category_id);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category", e);
            return false;
        }
    }

    // ─── Delete CategoryModel ──────────────────────────────────────
    
    private static final long SENTINEL_CATEGORY_ID = 1L;
    
    public boolean deleteCategory(long category_id) {
        String reassignSQL = "UPDATE transactions SET category_id = ? WHERE category_id = ?";
        String deleteSQL   = "DELETE FROM categories WHERE id = ? AND user_id IS NOT NULL";

       try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement reassignStmt = conn.prepareStatement(reassignSQL);
                 PreparedStatement deleteStmt   = conn.prepareStatement(deleteSQL)) {

                reassignStmt.setLong(1, SENTINEL_CATEGORY_ID);
                reassignStmt.setLong(2, category_id);
                reassignStmt.executeUpdate();

                deleteStmt.setLong(1, category_id);
                deleteStmt.executeUpdate();

                conn.commit();
                LOGGER.log(Level.INFO, "Category deleted id={0}", category_id);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error deleting category, rolled back", e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category", e);
            return false;
        }
    }

    // ─── Get Global Categories ────────────────────────────────

    public List<CategoryModel> getGlobalCategories() {
        String sql = "SELECT id, user_id, name, created_at " +
                     "FROM categories WHERE user_id IS NULL AND id != 1 " +
                     "ORDER BY name ASC";

        return fetchCategories(sql);
    }

    // ─── Get All Categories by User ───────────────────────────

    public List<CategoryModel> getCategoriesByUser(long user_id) {
        String sql = "SELECT id, user_id, name, created_at " +
                     "FROM categories " +
                     "WHERE (user_id IS NULL OR user_id = ?) AND id != 1 " +
                     "ORDER BY name ASC";

        List<CategoryModel> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching categories", e);
        }
        return categories;
    }

    // ─── Private Helpers ──────────────────────────────────────

    private List<CategoryModel> fetchCategories(String sql) {
        List<CategoryModel> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(mapRow(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching categories", e);
        }
        return categories;
    }

    private CategoryModel mapRow(ResultSet rs) throws SQLException {
        long id         = rs.getLong("id");
        long user_id_long = rs.getLong("user_id");
        Long user_id     = rs.wasNull() ? null : user_id_long;

        return new CategoryModel(
            id,
            user_id,
            rs.getString("name"),
            rs.getDate("created_at") != null
                ? rs.getDate("created_at").toLocalDate()
                : null
        );
    }
}