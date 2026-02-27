package com.tss.model;

import static com.tss.Utils.Constant.newNotificationId;

import java.time.LocalDateTime;
import java.util.Date;

public class Notification {
    public final long id;
    public final String description;
    public final LocalDateTime timeStamp;

    public Notification(String description){
        this.id = newNotificationId++;
        this.description = description;
        this.timeStamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "[NOTIFICATION] [" + timeStamp + "] " + description;
    }
}