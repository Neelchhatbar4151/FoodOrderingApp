package com.tss.model.User;

import com.tss.Datatype.Role;

import java.time.LocalDateTime;

public class Admin extends User {
    public Admin(long id, String name, String phone, String password, long seenNotificationCount, LocalDateTime createdOn) {
        super(id, name, phone, password, seenNotificationCount, createdOn);
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s %-15s %-20s",
                phone, name, createdOn);
    }
}