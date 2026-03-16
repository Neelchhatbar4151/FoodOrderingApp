package com.tss.model.User;

import com.tss.Datatype.Role;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Notification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class User {
    public final long id;
    protected final String name, phone, password;
    protected long seenNotificationCount;
    public final LocalDateTime createdOn;

    public User(long id, String name, String phone, String password, long seenNotificationCount, LocalDateTime createdOn) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.createdOn = createdOn;
        this.seenNotificationCount = seenNotificationCount;
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
}