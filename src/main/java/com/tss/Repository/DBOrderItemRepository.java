package com.tss.Repository;

import com.tss.DB.DBConnection;
import com.tss.Datatype.AvailabilityStatus;
import com.tss.model.Category;
import com.tss.model.FoodItem;
import com.tss.model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBOrderItemRepository implements OrderItemRepository {

    private DBOrderItemRepository() {
    }

    @Override
    public void addItem(long orderId, OrderItem item) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    INSERT INTO order_item(order_id, food_item_id, food_name, price_at_order, quantity)
                    VALUES (?, ?, ?, ?, ?)
                    """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, orderId);
            ps.setLong(2, item.foodItem.id);
            ps.setString(3, item.foodItem.name);
            ps.setDouble(4, item.foodItem.price);
            ps.setInt(5, item.getCurrentQuantity());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addItems(long orderId, List<OrderItem> items) {
        for (OrderItem item : items) {
            addItem(orderId, item);
        }
    }

    @Override
    public List<OrderItem> getByOrderId(long orderId) {
        List<OrderItem> items = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    SELECT oi.*, 
                           fi.food_item_id AS live_food_id,
                           fi.name AS live_food_name,
                           fi.description AS live_food_description,
                           fi.price AS live_food_price,
                           fi.is_available AS live_food_is_available,
                           fi.preparation_time AS live_food_preparation_time,
                           fi.calories AS live_food_calories,
                           c.category_id AS live_category_id,
                           c.name AS live_category_name
                    FROM order_item oi
                    LEFT JOIN food_items fi ON oi.food_item_id = fi.food_item_id
                    LEFT JOIN categories c ON fi.category_id = c.category_id
                    WHERE oi.order_id = ?
                    ORDER BY oi.order_item_id
                    """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, orderId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FoodItem foodItem;

                Object liveFoodId = rs.getObject("live_food_id");
                if (liveFoodId != null) {
                    Category category = null;
                    Object categoryId = rs.getObject("live_category_id");
                    if (categoryId != null) {
                        category = new Category(rs.getInt("live_category_id"), rs.getString("live_category_name"));
                    }

                    foodItem = new FoodItem.Builder(
                            rs.getString("live_food_name"),
                            rs.getDouble("live_food_price"),
                            category
                    )
                            .id(rs.getInt("live_food_id"))
                            .description(rs.getString("live_food_description"))
                            .availability(rs.getBoolean("live_food_is_available") ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.NOT_AVAILABLE)
                            .preparationTime(rs.getInt("live_food_preparation_time"))
                            .calories(rs.getInt("live_food_calories"))
                            .build();
                } else {
                    foodItem = new FoodItem.Builder(
                            rs.getString("food_name"),
                            rs.getDouble("price_at_order"),
                            null
                    )
                            .id(rs.getInt("food_item_id"))
                            .build();
                }

                items.add(new OrderItem(
                        rs.getLong("order_item_id"),
                        foodItem,
                        rs.getInt("quantity")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    @Override
    public boolean deleteByOrderId(long orderId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM order_item WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, orderId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static class Initiator {
        private static final DBOrderItemRepository instance = new DBOrderItemRepository();
    }

    public static DBOrderItemRepository getInstance() {
        return Initiator.instance;
    }
}
