package com.tss.Utils;

import com.tss.Repository.FoodItemRepository;
import com.tss.Subject.NotificationChannel;

import java.io.*;
import java.util.Scanner;

public class GlobalVariables {

    public static Scanner inputTaker = new Scanner(System.in);

    public int newOrderId = 1;
    public int newFoodItemId = 1;
    public long newNotificationId = 1;
    public int newCategoryId = 1;
    public int newUserId = 1;

    public NotificationChannel customerNotificationChannel = new NotificationChannel();
    public NotificationChannel deliveryPartnerNotificationChannel = new NotificationChannel();

    private GlobalVariables(){}

    private static class Initiator{
        private static final GlobalVariables instance = new GlobalVariables();
    }

    public static GlobalVariables getInstance(){
        return Initiator.instance;
    }

}
