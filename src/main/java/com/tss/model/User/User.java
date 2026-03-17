package com.tss.model.User;

import com.tss.Datatype.Role;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Notification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class User {
    public long id;
    protected String name, phone, password;
    protected List<Notification> notifications;
    protected long indexOfNewNotification;
    public Role role;
    public LocalDateTime createdOn;

    public User(String name, String phone, String password, Role role) {
        this.id = GlobalVariables.getInstance().newUserId++;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.notifications = new ArrayList<>();
        this.role = role;
        this.createdOn = LocalDateTime.now();
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
        return new ArrayList<>(notifications.subList(0, (int)indexOfNewNotification));
    }

    public void addNotification(Notification notification){
        notifications.add(notification);
    }

    public List<Notification> getNewNotifications(){
        List<Notification> newNotifications = new ArrayList<>(notifications.subList((int)indexOfNewNotification, notifications.size()));
        indexOfNewNotification = notifications.size();
        return newNotifications;
    }
}