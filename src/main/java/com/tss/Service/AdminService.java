package com.tss.Service;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.Repository.FoodItemRepository;
import com.tss.Repository.InMemoryFoodItemRepository;
import com.tss.Repository.UserRepository;
import com.tss.model.Category;
import com.tss.model.FoodItem;
import com.tss.model.Notification;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

import static com.tss.Utils.Constant.inputTaker;
import static com.tss.Utils.Input.*;
import static com.tss.Utils.Print.*;


public class AdminService {

    private final UserRepository userRepo;
    private final FoodItemRepository foodRepo;
    private final SelectorService selector;

    public AdminService(UserRepository userRepo){
        this.userRepo = userRepo;
        this.foodRepo = new InMemoryFoodItemRepository();
        this.selector = new SelectorService(userRepo, foodRepo);
    }

    public void addNewFoodItem(){
        info("Enter Food Item Name: ");
        String name = inputTaker.nextLine();

        info("Enter Food Item Price: ");
        double price = takeDouble();

        Category chosenCategory = selector.selectFoodCategory();

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

    public void addNewFoodCategory(){
        info("Enter Category Name: ");
        String name = inputTaker.nextLine();

        Category newCategory = new Category(name);

        foodRepo.addFoodCategory(newCategory);

        success(newCategory.name + " Category Successfully Added !");
    }

    public void removeFoodItem(){
        FoodItem item = selector.selectFoodItem();
        if(item == null) return;

        foodRepo.removeFoodItem(item.id);
        success("Item Successfully Removed !");
    }

    public void removeFoodCategory(){
        Category category = selector.selectFoodCategory();
        if(category == null) return;

        foodRepo.removeFoodCategory(category.id);
        success("Category Successfully Removed !");
    }

    public void toggleFoodItemAvailability(){
        FoodItem item = selector.selectFoodItem();
        if(item == null) return;

        info("Is Food Item Available(y/n): ");
        boolean result = takeBoolean();

        foodRepo.setFoodAvailability(item.id, (result? AvailabilityStatus.AVAILABLE:AvailabilityStatus.NOT_AVAILABLE));
        success("Food Item Availability Altered !");
    }

    public void approveDeliveryPartner(){
        DeliveryPartner deliveryPartner = selector.selectDeliveryPartner();
        if(deliveryPartner == null) return;

        deliveryPartner.setIsApproved(true);

        success("Approved Successfully !");
    }

    public void unApproveDeliveryPartner(){
        DeliveryPartner deliveryPartner = selector.selectDeliveryPartner();
        if(deliveryPartner == null) return;

        deliveryPartner.setIsApproved(false);

        success("Un Approved Successfully !");
    }

    public void notifyAllCustomers(){
        info("Enter Message: ");
        String msg = inputTaker.nextLine();

        Notification notification = new Notification(msg);

        for(User u: userRepo.getAllUsersInRole(Role.CUSTOMER)){
            u.addNotification(notification);
        }

        success("Notification sent to all Customers");
    }

    public void notifyAllDeliveryPartners(){
        info("Enter Message: ");
        String msg = inputTaker.nextLine();

        Notification notification = new Notification(msg);

        for(User u: userRepo.getAllUsersInRole(Role.DELIVERY_PARTNER)){
            u.addNotification(notification);
        }

        success("Notification sent to all Delivery Partners");
    }

    public void start(){
        while(true){
            try {
                info("""
                    ================== ADMIN MENU ==================
                    1.  Add New Food Item
                    2.  Add New Food Category
                    3.  Remove Food Item
                    4.  Remove Food Category
                    5.  Toggle Food Item Availability
                    6.  Create New Discount (NS)
                    7.  Create New Flat Discount (NS)
                    8.  Create New Percentage Discount (NS)
                    9.  Create New Festival Discount (NS)
                    10. Approve Delivery Partner
                    11. Unapprove Delivery Partner
                    12. Notify All Customers
                    13. Notify All Delivery Partners
                    14. Display All Food Items
                    15. Display All Food Categories
                    16. Change Delivery Partner Commission (NS)
                    0.  Go Back
                    ================================================
                    Enter your choice:""");

                int choice = takeInt();
                switch (choice) {
                    case 1 -> addNewFoodItem();
                    case 2 -> addNewFoodCategory();
                    case 3 -> removeFoodItem();
                    case 4 -> removeFoodCategory();
                    case 5 -> toggleFoodItemAvailability();
//                    case 6 -> createNewDiscount();
//                    case 7 -> createNewFlatDiscount();
//                    case 8 -> createNewPercentageDiscount();
//                    case 9 -> createNewFestivalDiscount();
                    case 10 -> approveDeliveryPartner();
                    case 11 -> unApproveDeliveryPartner();
                    case 12 -> notifyAllCustomers();
                    case 13 -> notifyAllDeliveryPartners();
                    case 14 -> selector.displayAllFoodItems(foodRepo.getAllFoodItems());
                    case 15 -> selector.displayAllCategories(foodRepo.getAllFoodCategories());
                    case 0 -> {
                        success("<--Back");
                        return;
                    }
                    default -> failure("Invalid choice.");
                }
            } catch (Exception e) {
                exception(e);
            }
        }
    }
}
