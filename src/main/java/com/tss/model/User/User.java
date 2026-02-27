package com.tss.model.User;

import com.tss.Datatype.Role;
import com.tss.model.Notification;
import static com.tss.Utils.Constant.newUserId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class User {
    public final int id;
    protected final String name, phone, password;
    protected final List<Notification> notifications;
    protected int indexOfNewNotification;
    public final Role role;
    public final LocalDate createdOn;

    public User(String name, String phone, String password, Role role) {
        this.id = newUserId++;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.notifications = new ArrayList<>();
        this.role = role;
        this.createdOn = LocalDate.now();
        this.indexOfNewNotification = 0;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public List<Notification> getOldNotifications() {
        return new ArrayList<>(notifications.subList(0, indexOfNewNotification));
    }

    public void addNotification(Notification notification){
        notifications.add(notification);
    }

    public List<Notification> getNewNotifications(){
        List<Notification> newNotifications = new ArrayList<>(notifications.subList(indexOfNewNotification, notifications.size()));
        indexOfNewNotification = notifications.size();
        return newNotifications;
    }
}