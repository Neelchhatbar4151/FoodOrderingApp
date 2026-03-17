package com.tss.Repository;

import com.tss.DB.DBConnection;
import com.tss.model.Notification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Singleton
public class DBNotificationRepository implements NotificationRepository {

    private DBNotificationRepository() {}

    // ================================
    // 🔹 ADD PERSONAL NOTIFICATION
    // ================================
    @Override
    public void addForUser(long userId, String description) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                INSERT INTO notification(description, user_id, created_on)
                VALUES (?, ?, now())
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, description);
            ps.setLong(2, userId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================================
    // 🔹 BROADCAST TO ALL
    // ================================
    @Override
    public void broadcastToAll(String description) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                INSERT INTO notification(description, is_broadcast, created_on)
                VALUES (?, true, now())
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, description);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================================
    // 🔹 BROADCAST TO ROLE
    // ================================
    @Override
    public void broadcastToRole(String description, String role) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                INSERT INTO notification(description, is_broadcast, role, created_on)
                VALUES (?, true, ?::role, now())
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, description);
            ps.setString(2, role); // must match enum

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================================
    // 🔥 GET ALL RELEVANT NOTIFICATIONS
    // ================================
    @Override
    public List<Notification> getNotificationsForUser(long userId, String role) {

        List<Notification> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT * FROM notification
                WHERE
                    user_id = ?
                    OR is_broadcast = TRUE
                    OR (is_broadcast = TRUE AND role = ?)
                ORDER BY created_on DESC
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, userId);
            ps.setString(2, role);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(buildNotification(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================================
    // 🔹 GET BY ID
    // ================================
    @Override
    public Notification getById(long notificationId) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT * FROM notification WHERE notification_id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, notificationId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return buildNotification(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================================
    // 🔹 DELETE
    // ================================
    @Override
    public boolean deleteNotification(long notificationId) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = "DELETE FROM notification WHERE notification_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, notificationId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================================
    // 🔥 HELPER
    // ================================
    private Notification buildNotification(ResultSet rs) throws Exception {

        return new Notification(
                rs.getInt("notification_id"),
                rs.getString("description"),
                rs.getTimestamp("created_on").toLocalDateTime()
        );
    }

    // ================================
    // 🔹 SINGLETON
    // ================================
    public static class Initiator {
        private static final DBNotificationRepository instance =
                new DBNotificationRepository();
    }

    public static DBNotificationRepository getInstance() {
        return Initiator.instance;
    }
}