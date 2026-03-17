package com.tss.Subject;

import com.tss.Observer.NotificationObserver;
import com.tss.model.Notification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationChannel {
    private final Set<NotificationObserver> observers = new HashSet<>();

    public void subscribe(NotificationObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Notification notification) {
        for (NotificationObserver observer : observers) {
            observer.addNotification(notification);
        }
    }
}