package com.tss.model;

import com.tss.Utils.GlobalVariables;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
    public final long id;
    public final String description;
    public final LocalDateTime timeStamp;

    public Notification(String description){
        this.id = GlobalVariables.getInstance().newNotificationId++;
        this.description = description;
        this.timeStamp = LocalDateTime.now();
    }
    public Notification(int id, String description, LocalDateTime timeStamp){
        this.id = id;
        this.description = description;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return "[NOTIFICATION] [" + timeStamp.format(formatter) + "] " + description;
    }
}