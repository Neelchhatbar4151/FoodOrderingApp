package com.tss.Repository;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.DB.DBConnection;
import com.tss.model.Category;
import com.tss.model.FoodItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//Singleton
public class DBFoodItemRepository implements FoodItemRepository {

    private DBFoodItemRepository(){}

    public static class Initiator {
        private static final DBFoodItemRepository instance = new DBFoodItemRepository();
    }

    public static DBFoodItemRepository getInstance() {
        return Initiator.instance;
    }

    // ================================
    // 🔹 ADD FOOD ITEM
    // ================================
    @Override
    public void addFoodItem(FoodItem item) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                INSERT INTO food_items
                (name, description, price, category_id, availability, preparation_time, calories)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, item.name);
            ps.setString(2, item.description);
            ps.setDouble(3, item.price);
            ps.setObject(4, item.category != null ? item.category.id : null);
            ps.setString(5, item.getAvailability().name());
            ps.setInt(6, item.preparationTime);
            ps.setInt(7, item.calories);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================================
    // 🔹 REMOVE FOOD ITEM
    // ================================
    @Override
    public boolean removeFoodItem(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = "DELETE FROM food_items WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================================
    // 🔹 GET FOOD ITEM BY ID
    // ================================
    @Override
    public FoodItem getFoodItemById(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT fi.*, c.name AS category_name
                FROM food_items fi
                LEFT JOIN categories c ON fi.category_id = c.id
                WHERE fi.id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return buildFoodItem(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================================
    // 🔹 GET ALL FOOD ITEMS
    // ================================
    @Override
    public List<FoodItem> getAllFoodItems() {

        List<FoodItem> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT fi.*, c.name AS category_name
                FROM food_items fi
                LEFT JOIN categories c ON fi.category_id = c.id
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(buildFoodItem(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================================
    // 🔹 UPDATE AVAILABILITY
    // ================================
    @Override
    public boolean setFoodAvailability(int id, AvailabilityStatus state) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                UPDATE food_items
                SET availability = ?
                WHERE id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, state.name());
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================================
    // 🔹 CATEGORY METHODS
    // ================================
    @Override
    public void addFoodCategory(Category category) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = "INSERT INTO categories(name) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, category.name);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeFoodCategory(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            // 🔹 set category_id null for food items
            PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE food_items SET category_id = NULL WHERE category_id = ?"
            );
            ps1.setInt(1, id);
            ps1.executeUpdate();

            // 🔹 delete category
            PreparedStatement ps2 = conn.prepareStatement(
                    "DELETE FROM categories WHERE id = ?"
            );
            ps2.setInt(1, id);

            return ps2.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Category getFoodCategoryById(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT * FROM categories WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return new Category(
                    rs.getInt("id"),
                    rs.getString("name")
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Category> getAllFoodCategories() {

        List<Category> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT * FROM categories";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================================
    // 🔥 HELPER
    // ================================
    private FoodItem buildFoodItem(ResultSet rs) throws Exception {

        Category category = null;

        int categoryId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            category = new Category(categoryId, rs.getString("category_name"));
        }

        return new FoodItem.Builder(
                rs.getString("name"),
                rs.getDouble("price"),
                category
        )
                .id(rs.getInt("id"))
                .description(rs.getString("description"))
                .availability(AvailabilityStatus.valueOf(rs.getString("availability")))
                .preparationTime(rs.getInt("preparation_time"))
                .calories(rs.getInt("calories"))
                .build();
    }
}
