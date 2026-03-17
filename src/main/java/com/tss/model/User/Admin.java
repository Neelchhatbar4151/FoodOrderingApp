package com.tss.model.User;

import com.tss.Datatype.Role;
import com.tss.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public class Admin extends User {
    public Admin(String name, String phone, String password) {
        super(name, phone, password, Role.ADMIN);
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s %-15s %-20s",
                role, phone, name, createdOn);
    }

    // ✅ FULL BUILDER
    public static class Builder {

        private long id;
        private String name;
        private String phone;
        private String password;
        private List<Notification> notifications;
        private int indexOfNewNotification;
        private Role role;
        private LocalDateTime createdOn;

        public Builder setId(long id) { this.id = id; return this; }
        public Builder setName(String name) { this.name = name; return this; }
        public Builder setPhone(String phone) { this.phone = phone; return this; }
        public Builder setPassword(String password) { this.password = password; return this; }
        public Builder setNotifications(List<Notification> notifications) { this.notifications = notifications; return this; }
        public Builder setIndexOfNewNotification(int index) { this.indexOfNewNotification = index; return this; }
        public Builder setRole(Role role) { this.role = role; return this; }
        public Builder setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; return this; }

        public Admin build() {
            Admin a = new Admin(name, phone, password);

            a.id = id;
            a.name = name;
            a.phone = phone;
            a.password = password;
            a.notifications = notifications;
            a.indexOfNewNotification = indexOfNewNotification;
            a.role = role;
            a.createdOn = createdOn;

            return a;
        }
    }
}