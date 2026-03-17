package com.tss.Service;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.Exception.NoDataFoundException;
import com.tss.Repository.FoodItemRepository;
import com.tss.Repository.UserRepository;
import com.tss.Utils.Display;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Category;
import com.tss.model.FoodItem;
import com.tss.model.Notification;
import com.tss.model.User.DeliveryPartner;

import static com.tss.Utils.GlobalVariables.inputTaker;
import static com.tss.Utils.Input.*;
import static com.tss.Utils.Print.*;


public class AdminService {

    private final UserRepository userRepo;
    private final FoodItemRepository foodRepo;
    private final SelectorService selector;

    public AdminService(){
        this.userRepo = GlobalVariables.getInstance().userRepository;
        this.foodRepo = GlobalVariables.getInstance().foodItemRepository;
        this.selector = new SelectorService();
    }

    private void addNewFoodItem(){
        Category chosenCategory = selector.selectFoodCategory();

        info("Enter Food Item Name: ");
        String name = inputTaker.nextLine();

        info("Enter Food Item Price: ");
        double price = takeDouble();

        info("Enter Food Item Description: ");
        String description = inputTaker.nextLine();

        info("Enter Food Item Preparation Time (In Minutes) : ");
        int preparationMinutes = takeInt();

        info("Enter Food Item Calories: ");
        int calories = takeInt();

        FoodItem newItem = new FoodItem.Builder(name, price, chosenCategory)
                .calories(calories)
                .description(description)
                .preparationTime(preparationMinutes)
                .build();

        foodRepo.addFoodItem(newItem);
        success(newItem.name + " Successfully Added !");
    }

    private void addNewFoodCategory(){
        info("Enter Category Name: ");
        String name = inputTaker.nextLine();

        Category newCategory = new Category(name);

        foodRepo.addFoodCategory(newCategory);

        success(newCategory.name + " Category Successfully Added !");
    }

    private void removeFoodItem(){
        FoodItem item = selector.selectFoodItem();
        if(item == null) return;

        foodRepo.removeFoodItem(item.id);
        success("Item Successfully Removed !");
    }

    private void removeFoodCategory(){
        Category category = selector.selectFoodCategory();
        if(category == null) return;

        foodRepo.removeFoodCategory(category.id);
        success("Category Successfully Removed !");
    }

    private void toggleFoodItemAvailability(){
        FoodItem item = selector.selectFoodItem();
        if(item == null) return;

        info("Is Food Item Available(y/n): ");
        boolean result = takeBoolean();

        foodRepo.setFoodAvailability(item.id, (result? AvailabilityStatus.AVAILABLE:AvailabilityStatus.NOT_AVAILABLE));
        success("Food Item Availability Altered !");
    }

    private void approveDeliveryPartner(){
        DeliveryPartner deliveryPartner = selector.selectDeliveryPartner();
        if(deliveryPartner == null) return;

        if(deliveryPartner.getIsApproved()){
            throw new IllegalStateException("This delivery partner is already approved.");
        }

        deliveryPartner.addNotification(new Notification("You've Been approved as a Delivery Partner, Now the orders can be assigned to you..."));
        deliveryPartner.setIsApproved(true);
        success("Approved Successfully !");
    }

    private void unApproveDeliveryPartner(){
        DeliveryPartner deliveryPartner = selector.selectDeliveryPartner();
        if(deliveryPartner == null) return;

        if(!deliveryPartner.getIsApproved()){
            throw new IllegalStateException("This delivery partner is already not approved.");
        }

        deliveryPartner.setIsApproved(false);
        deliveryPartner.addNotification(new Notification("You've Been unapproved as a Delivery Partner, Now the orders can't be assigned to you..."));
        success("Unapproved Successfully !");
    }

    private Notification takeNotification(){
        info("Enter Message: ");
        String msg = inputTaker.nextLine();

        return new Notification(msg);
    }

    private void notifyAllCustomers(){
        Notification notification = takeNotification();

        GlobalVariables.getInstance()
                .customerNotificationChannel
                .notifyObservers(notification);

        success("Notification sent to all Customers");
    }

    private void notifyAllDeliveryPartners(){
        Notification notification = takeNotification();

        GlobalVariables.getInstance()
                .deliveryPartnerNotificationChannel
                .notifyObservers(notification);

        success("Notification sent to all Delivery Partners");
    }

    private void changeDeliveryPartnerCommission() {
        info("Enter Changed Commission: ");
        double commission = takeDouble();

        if(commission < 0 || commission > 100){
            throw new IllegalArgumentException("Invalid Value.");
        }

        DeliveryPartner.commissionPercentage = (commission/100);

        Notification notification = new Notification("Commission Per Order Changed to " + (commission) + " %.");

        userRepo.getAllUsersInRole(Role.DELIVERY_PARTNER)
                .forEach((u) -> u.addNotification(notification));

        success("Notification sent to all Delivery Partners");
    }


    private void displayAllDeliveryPartners() {

        if(userRepo.getAllUsersInRole(Role.DELIVERY_PARTNER).isEmpty()){
            throw new NoDataFoundException("Delivery Partners");
        }

        Display.displayDeliveryPartnerHeading();

        userRepo.getAllUsersInRole(Role.DELIVERY_PARTNER)
                .forEach(( i)->success(i.toString()));
    }

    private void displayAllFoodItems(){
        if(foodRepo.getAllFoodItems().isEmpty()){
            throw new NoDataFoundException("Food Items");
        }
        Display.displayFoodItemHeading();
        foodRepo.getAllFoodItems()
                .forEach((i)->success(i.toString()));
    }

    private void displayAllFoodCategories(){
        if(foodRepo.getAllFoodCategories().isEmpty()){
            throw new NoDataFoundException("Food Categories");
        }
        Display.displayFoodCategoryHeading();
        foodRepo.getAllFoodCategories()
                .forEach((i) -> success(i.toString()));
    }

    private void displayOrderQueue(){
        OrderService.getInstance().printOrderQueue();
    }

    private void displayDeliveryPartnerQueue(){
        OrderService.getInstance().printDeliveryPartnerQueue();
    }

    public void start(){
        while(true){
            try {
                if ( !process() ) return;
            } catch (Exception e) {
                exception(e);
            }
        }
    }

    private boolean process(){
        Display.displayAdminMenu();

        int choice = takeInt();
        switch (choice) {
            case 1 -> manageFoodItems();
            case 2 -> manageDeliveryPartners();
            case 3 -> sendNotifications();
            case 4 -> view();
            case 0 -> {
                success("<--Back");
                return false;
            }
            default -> throw new IllegalArgumentException("Invalid Option Selected.");
        }
        return true;
    }

    private void view() {
        while(true){
            try {
                Display.displayViewMenu();

                int choice = takeInt();
                switch (choice) {
                    case 1 -> displayAllFoodItems();

                    case 2 -> displayAllFoodCategories();

                    case 3 -> displayAllDeliveryPartners();

                    case 4 -> {
                        success("| ORDER QUEUE");
                        displayOrderQueue();
                        System.out.println();
                        success("| DELIVERY PARTNER QUEUE");
                        displayDeliveryPartnerQueue();
                    }

                    case 0 -> {
                        success("<--Back");
                        return ;
                    }
                    default -> throw new IllegalArgumentException("Invalid Option Selected.");
                }
            } catch (Exception e) {
                exception(e);
            }
        }
    }

    private void sendNotifications() {
        while(true){
            try {
                Display.displaySendNotificationMenu();

                int choice = takeInt();
                switch (choice) {
                    case 1 -> notifyAllCustomers();

                    case 2 -> notifyAllDeliveryPartners();

                    case 0 -> {
                        success("<--Back");
                        return ;
                    }
                    default -> throw new IllegalArgumentException("Invalid Option Selected.");
                }
            } catch (Exception e) {
                exception(e);
            }
        }
    }

    private void manageDeliveryPartners() {
        while(true){
            try {
                Display.displayManageDeliveryPartnersMenu();

                int choice = takeInt();
                switch (choice) {
                    case 1 -> approveDeliveryPartner();
                    case 2 -> unApproveDeliveryPartner();

                    case 3 -> changeDeliveryPartnerCommission();

                    case 0 -> {
                        success("<--Back");
                        return ;
                    }
                    default -> throw new IllegalArgumentException("Invalid Option Selected.");
                }
            } catch (Exception e) {
                exception(e);
            }
        }
    }

    private void manageFoodItems(){
        while(true){
            try {
                Display.displayManageFoodItemsMenu();

                int choice = takeInt();
                switch (choice) {
                    case 1 -> addNewFoodItem();
                    case 2 -> removeFoodItem();

                    case 3 -> addNewFoodCategory();
                    case 4 -> removeFoodCategory();

                    case 5 -> toggleFoodItemAvailability();

                    case 0 -> {
                        success("<--Back");
                        return ;
                    }
                    default -> throw new IllegalArgumentException("Invalid Option Selected.");
                }
            } catch (Exception e) {
                exception(e);
            }
        }
    }
}
