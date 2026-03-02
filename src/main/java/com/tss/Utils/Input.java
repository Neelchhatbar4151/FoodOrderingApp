package com.tss.Utils;

import java.util.InputMismatchException;
import java.util.regex.Pattern;

import static com.tss.Utils.GlobalVariables.inputTaker;
import static com.tss.Utils.Print.*;

public class Input {

    private static final Pattern INDIAN_PHONE_PATTERN = Pattern.compile("\\d{10}");

    public static boolean isValidIndianMobile(String phone) {
        return phone != null && INDIAN_PHONE_PATTERN.matcher(phone).matches();
    }

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
                inputTaker.nextLine();
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
                inputTaker.nextLine();
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
                inputTaker.nextLine();
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
                inputTaker.nextLine();
                exception(e);
                info("Enter y/n: ");
            }
        }
    }

    public static String takePhone(){
        while(true){
            try{
                String phone = inputTaker.next();
                if(!isValidIndianMobile(phone)){
                    throw new IllegalArgumentException("Invalid Format, Phone Number Should Contain Exactly 10 Digits.");
                }
                else if(phone.charAt(0) < '6' || phone.charAt(0) > '9'){
                    throw new IllegalArgumentException("Invalid Format, Phone Number Should Start with 6, 7, 8 or 9.");
                }
                inputTaker.nextLine();
                return phone;

            } catch(RuntimeException e){
                inputTaker.nextLine();
                exception(e);
                info("Enter Phone Number: ");
            }
        }
    }
}
