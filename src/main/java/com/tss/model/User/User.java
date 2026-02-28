package com.tss.model.User;

import com.tss.Datatype.Role;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Notification;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class User implements Serializable {
    public final int id;
    protected final String name, phone, password;
    protected final List<Notification> notifications;
    protected int indexOfNewNotification;
    public final Role role;
    public final LocalDate createdOn;

    public User(String name, String phone, String password, Role role) {
        this.id = GlobalVariables.getInstance().newUserId++;
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

    public boolean matchPassword(String password){
        return this.password.equals(password);
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