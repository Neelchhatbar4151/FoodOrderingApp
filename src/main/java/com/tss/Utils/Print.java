package com.tss.Utils;

import static com.tss.Utils.GlobalVariables.inputTaker;

public class Print {
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static void success(String msg){
        System.out.println(ANSI_GREEN + msg + ANSI_RESET);
    }
    public static void failure(String msg){
        System.out.println(ANSI_RED + msg + ANSI_RESET);
    }
    public static void info(String msg){
        System.out.println(ANSI_BLUE + msg + ANSI_RESET);
    }
    public static void exception(Exception e){
//        failure(e.getClass().getSimpleName());
        failure("[ERROR] " + (e.getMessage() != null?e.getMessage():"Invalid."));
//        failure(e.getMessage());
        System.out.println("Press Enter to Continue...");
        inputTaker.nextLine();
    }
}