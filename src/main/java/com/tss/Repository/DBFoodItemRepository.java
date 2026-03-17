package com.tss.Repository;

import com.tss.DB.DBConnection;
import com.tss.Datatype.AvailabilityStatus;
import com.tss.model.Category;
import com.tss.model.FoodItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBFoodItemRepository implements FoodItemRepository {

    private DBFoodItemRepository(){}

    public static class Initiator {
        private static final DBFoodItemRepository instance = new DBFoodItemRepository();
    }

    public static DBFoodItemRepository getInstance() {
        return Initiator.instance;
    }

    @Override
    public void addFoodItem(FoodItem item) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                INSERT INTO food_items
                (name, description, price, category_id, is_available, preparation_time, calories)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, item.name);
            ps.setString(2, item.description);
            ps.setDouble(3, item.price);
            ps.setObject(4, item.category != null ? item.category.id : null);
            ps.setBoolean(5, (item.getAvailability() == AvailabilityStatus.AVAILABLE));
            ps.setInt(6, item.preparationTime);
            ps.setInt(7, item.calories);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeFoodItem(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = "DELETE FROM food_items WHERE food_item_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public FoodItem getFoodItemById(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT fi.*, c.name AS category_name
                FROM food_items fi
                LEFT JOIN categories c ON fi.category_id = c.category_id
                WHERE fi.food_item_id = ?
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

    @Override
    public List<FoodItem> getAllFoodItems() {

        List<FoodItem> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT fi.*, c.name AS category_name
                FROM food_items fi
                LEFT JOIN categories c ON fi.category_id = c.category_id
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

    @Override
    public boolean setFoodAvailability(int id, AvailabilityStatus state) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                UPDATE food_items
                SET is_available = ?
                WHERE food_item_id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setBoolean(1, state == AvailabilityStatus.AVAILABLE);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

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

            PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE food_items SET category_id = NULL WHERE category_id = ?"
            );
            ps1.setInt(1, id);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "DELETE FROM categories WHERE category_id = ?"
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

            String query = "SELECT * FROM categories WHERE category_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return new Category(
                    rs.getInt("category_id"),
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
                        rs.getInt("category_id"),
                        rs.getString("name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

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
                .id(rs.getInt("food_item_id"))
                .description(rs.getString("description"))
                .availability((rs.getBoolean("is_available")?AvailabilityStatus.AVAILABLE:AvailabilityStatus.NOT_AVAILABLE))
                .preparationTime(rs.getInt("preparation_time"))
                .calories(rs.getInt("calories"))
                .build();
    }
}
