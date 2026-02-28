package com.tss.Utils;

import com.tss.Repository.InMemoryUserRepository;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class GlobalVariables implements Serializable {
    private static final String filePath = "./Data/globalVariables.ser";

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
        private static final GlobalVariables instance = load();
        private static GlobalVariables load(){
            File file = new File(filePath);

            if (!file.exists() || file.length() == 0) {
                return new GlobalVariables();
            }
            try (ObjectInputStream in =
                         new ObjectInputStream(new FileInputStream(filePath))) {

                GlobalVariables loadedInstance = (GlobalVariables) in.readObject();
                return Objects.requireNonNullElseGet(loadedInstance, GlobalVariables::new);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static GlobalVariables getInstance(){
        return Initiator.instance;
    }

    public void saveState(){
        try(ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(filePath))){
            out.writeObject(this);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
