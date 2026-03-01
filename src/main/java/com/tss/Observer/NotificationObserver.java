package com.tss.Observer;

import com.tss.model.Notification;

public interface NotificationObserver {
    void addNotification(Notification message);
}