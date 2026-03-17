package com.tss.Repository;

import com.tss.model.Notification;

import java.util.List;

public interface NotificationRepository {

    // 🔹 Create
    void addForUser(long userId, String description);

    void broadcastToAll(String description);

    void broadcastToRole(String description, String role);

    // 🔹 Read
    List<Notification> getNotificationsForUser(long userId, String role);

    Notification getById(long notificationId);

    // 🔹 Delete
    boolean deleteNotification(long notificationId);
}