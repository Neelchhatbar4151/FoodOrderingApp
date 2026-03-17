package com.tss;

import com.tss.Datatype.Role;
import com.tss.Exception.AuthenticationFailedException;
import com.tss.Repository.UserRepository;
import com.tss.Utils.Display;
import com.tss.Utils.GlobalVariables;
import com.tss.model.CurrentUser;
import com.tss.model.User.*;

import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.tss.Utils.GlobalVariables.inputTaker;
import static com.tss.Utils.Input.*;
import static com.tss.Utils.Print.*;

public class MiniFoodOrderingApp {
    private final UserRepository userRepo;

    public MiniFoodOrderingApp(){
        this.userRepo = GlobalVariables.getInstance().userRepository;
    }

    public void registerUser(Role role){
        info("Enter Your Name: ");
        String name = inputTaker.nextLine();

        info("Enter Your Phone Number: ");
        String phone = takePhone();

        info("Enter a Password: ");
        String password = inputTaker.nextLine();

        if(!userRepo.addNewUser(role.createUser(name, phone, password))){
            throw new IllegalArgumentException(role + " With Same Phone Number Already Exists.");
        }

        success(role.name() + " Registered Successfully !");
    }

    private void initiateService(Role role){
        role.startService();
    }

    public void loginUser(Role role){
        info("Enter Phone: ");
        String phone = takePhone();

        info("Enter password: ");
        String password = inputTaker.nextLine();

        User user = userRepo.getUser(phone, password, role);
        if(user == null){
            throw new AuthenticationFailedException();
        }
        else{
            success("Correct Credentials.");
            success("You're Logged in as " + user.getName() + " [ " + role + " ]");

            //Setting up global access to current user
            CurrentUser.getInstance().setUser(user);

            //Handing over control to respective role based services
            initiateService(role);
        }
    }

    public void start(){
        while(true){
            try {
                if ( !process() ) return;
            }
            catch(NoSuchElementException e){
                failure("File Input has been ended, continuing with user input...");
                inputTaker = new Scanner(System.in);
            }
            catch (Exception e) {
                exception(e);
            }
        }
    }

    public boolean process(){
        Display.displayMainMenu();

        int choice = takeInt();
        switch (choice) {
            case 1 -> registerUser(Role.CUSTOMER);
            case 2 -> registerUser(Role.DELIVERY_PARTNER);
            case 3 -> loginUser(Role.ADMIN);
            case 4 -> loginUser(Role.CUSTOMER);
            case 5 -> loginUser(Role.DELIVERY_PARTNER);
            case 0 -> {
                failure("X-- Closing App");
                return false;
            }
            default -> throw new IllegalArgumentException("Invalid Option Selected.");

        }

        //Clearing current user session
        CurrentUser.getInstance().setUser(null);
        return true;
    }
}
