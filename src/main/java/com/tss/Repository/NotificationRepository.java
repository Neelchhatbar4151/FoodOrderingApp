package com.tss.Repository;

import com.tss.model.Notification;

import java.util.List;

public interface NotificationRepository {

    void addForUser(long userId, String description);

    void broadcastToAll(String description);

    void broadcastToRole(String description, String role);

    List<Notification> getNotificationsForUser(long userId, String role);

    Notification getById(long notificationId);

    boolean deleteNotification(long notificationId);
}