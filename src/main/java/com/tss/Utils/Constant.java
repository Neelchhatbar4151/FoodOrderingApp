package com.tss.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Constant {

    public static long newNotificationId = 1;
    public static int newUserId = 1;
    public static int newOrderId = 1;
    public static double commissionPercentage = 0.1;
    public static Scanner inputTaker;
    static {
        try {
            File file = new File("input.txt");
            inputTaker = new Scanner(file);
//            inputTaker = new Scanner(System.in);
        } catch (Exception f) {
            System.out.println("File Not Found.");
        }
    }
    public static int newFoodItemId = 1;
    public static int newCategoryId = 1;


    private Constant(){}
}
