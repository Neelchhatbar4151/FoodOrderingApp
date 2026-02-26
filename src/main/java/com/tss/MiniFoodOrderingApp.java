package com.tss;

import com.tss.Datatype.Role;
import com.tss.Repository.InMemoryUserRepository;
import com.tss.Repository.UserRepository;
import com.tss.Service.AdminService;
import com.tss.Service.CustomerService;
import com.tss.Service.DeliveryPartnerService;
import com.tss.Service.OrderService;
import com.tss.Utils.Display;
import com.tss.model.CurrentUser;
import com.tss.model.User.*;

import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.tss.Utils.Constant.inputTaker;
import static com.tss.Utils.Input.*;
import static com.tss.Utils.Print.*;

public class MiniFoodOrderingApp {
    private final UserRepository userRepo;

    public MiniFoodOrderingApp(){
        this.userRepo = new InMemoryUserRepository();
    }

    public void registerUser(Role role){
        info("Enter Your Name: ");
        String name = inputTaker.nextLine();

        info("Enter Your Phone Number: ");
        String phone = inputTaker.nextLine();

        info("Enter a Password: ");
        String password = inputTaker.nextLine();

        User user;

        if(role == Role.CUSTOMER){
            user = new Customer(name, phone, password);
        }
        else if(role == Role.ADMIN){
            user = new Admin(name, phone, password);
        }
        else{
            user = new DeliveryPartner(name, phone, password);
        }

        if(!userRepo.addNewUser(user)){
            failure(role + " With Same Phone Number Already Exists.");
            return;
        }

        success(role.name() + " Registered Successfully !");
    }

    private void initiateService(Role role){
        if(role == Role.CUSTOMER){
            new CustomerService(userRepo).start();
        }
        else if(role == Role.ADMIN){
            new AdminService(userRepo).start();
        }
        else{
            new DeliveryPartnerService().start();
        }
    }

    public void loginUser(Role role){
        info("Enter Phone: ");
        String phone = inputTaker.nextLine();

        info("Enter password: ");
        String password = inputTaker.nextLine();

        User user = userRepo.getUser(phone, password, role);
        if(user == null){
            failure("Incorrect Credentials.");
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
                        return;
                    }
                    default -> failure("Invalid Option Selected.");
                }

                //Clearing current user session
                CurrentUser.getInstance().setUser(null);
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
}
