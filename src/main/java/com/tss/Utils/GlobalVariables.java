package com.tss.Utils;

import java.io.*;
import java.util.Scanner;

public class GlobalVariables {

    public static Scanner inputTaker;
    static {
        try {
            File file = new File("input.txt");
//            inputTaker = new Scanner(file);
            inputTaker = new Scanner(System.in);
        } catch (Exception f) {
            System.out.println("File Not Found.");
        }
    }

    public int newOrderId = 1;
    public int newFoodItemId = 1;
    public long newNotificationId = 1;
    public int newCategoryId = 1;
    public int newUserId = 1;


    private GlobalVariables(){}

    private static class Initiator{
        private static final GlobalVariables instance = new GlobalVariables();
    }

    public static GlobalVariables getInstance(){
        return Initiator.instance;
    }

}
