package com.tss.Utils;

import static com.tss.Utils.Print.exception;
import static com.tss.Utils.Print.info;
import static com.tss.Utils.Constant.inputTaker;

public class Input {

    public static int takeInt(){
        while(true){
            try{
                int number = inputTaker.nextInt();
                if(number >= 0) {
                    inputTaker.nextLine();
                    return number;
                }

                throw new IllegalArgumentException("Only Positive Numbers Are Allowed.");
            }
            catch(Exception e){
                exception(e);
                info("Enter : ");
            }
        }
    }

    public static double takeDouble(){
        while(true){
            try{
                double number = inputTaker.nextDouble();
                if(number >= 0) {
                    inputTaker.nextLine();
                    return number;
                }

                throw new IllegalArgumentException("Only Positive Numbers Are Allowed.");
            }
            catch(Exception e){
                exception(e);
                info("Enter : ");
            }
        }
    }

    public static long takeLong(){
        while(true){
            try{
                long number = inputTaker.nextLong();
                if(number >= 0) {
                    inputTaker.nextLine();
                    return number;
                }

                throw new IllegalArgumentException("Only Positive Numbers Are Allowed.");
            }
            catch(Exception e){
                exception(e);
                info("Enter: ");
            }
        }
    }

    public static boolean takeBoolean(){
        while(true){
            try {
                String yesOrNo = inputTaker.next();
                if (yesOrNo.equals("y")) return true;

                if (yesOrNo.equals("n")) return false;

                throw new IllegalArgumentException("Enter Valid Choice.");
            } catch (RuntimeException e) {
                exception(e);
                info("Enter y/n: ");
            }
        }
    }
}
