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

    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return "[NOTIFICATION] [" + timeStamp.format(formatter) + "] " + description;
    }
}