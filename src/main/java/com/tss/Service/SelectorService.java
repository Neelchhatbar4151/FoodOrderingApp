package com.tss.Service;

import com.tss.Datatype.Role;
import com.tss.Exception.EmptyCartException;
import com.tss.Exception.NoDataFoundException;
import com.tss.Repository.FoodItemRepository;
import com.tss.Repository.UserRepository;
import com.tss.Utils.Display;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Category;
import com.tss.model.FoodItem;
import com.tss.model.Order;
import com.tss.model.OrderItem;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

import java.util.List;

import static com.tss.Utils.Input.takeInt;
import static com.tss.Utils.Print.*;
import static com.tss.Utils.Print.success;

public class SelectorService {

    private final UserRepository userRepo;
    private final FoodItemRepository foodRepo;

    public SelectorService(){
        this.userRepo = GlobalVariables.getInstance().userRepository;
        this.foodRepo = GlobalVariables.getInstance().foodItemRepository;
    }

    public Category selectFoodCategory(){
        List<Category> categories = foodRepo.getAllFoodCategories();

        if(categories.isEmpty()){
            throw new NoDataFoundException("Categories");
        }

        Category chosenCategory = null;
        while(true) {
            info("Available Food Categories: ");
            Display.displayFoodCategoryHeading();
            categories.forEach((i) -> success(i.toString()));
            info("Enter Category Id: ");
            int id = takeInt();
            chosenCategory = foodRepo.getFoodCategoryById(id);
            if(chosenCategory != null){
                break;
            }
            failure("Invalid Category Id.");
        }

        return chosenCategory;
    }

    public FoodItem selectFoodItem(){
        List<FoodItem> foodItems = foodRepo.getAllFoodItems();

        if(foodItems.isEmpty()){
            throw new NoDataFoundException("Food Items");
        }

        FoodItem chosenFoodItem = null;
        while(true) {
            info("Available Food Items: ");
            Display.displayFoodItemHeading();
            foodItems.forEach((i) -> success(i.toString()));
            info("Enter Food Item Id: ");
            int id = takeInt();
            chosenFoodItem = foodRepo.getFoodItemById(id);
            if(chosenFoodItem != null){
                break;
            }
            failure("Invalid Food Item Id.");
        }

        return chosenFoodItem;
    }

    public DeliveryPartner selectDeliveryPartner(){
        List<User> users = userRepo.getAllUsersInRole(Role.DELIVERY_PARTNER);

        if(users.isEmpty()){
            throw new NoDataFoundException("Delivery Partners");
        }

        DeliveryPartner chosenDeliveryPartner = null;
        while(true) {
            info("Available Delivery Partners: ");
            Display.displayDeliveryPartnerHeading();
            users.forEach((i) -> success(i.toString()));
            info("Enter Delivery Partner id: ");
            int id = takeInt();
            User user = userRepo.getUserById(id);
            if(user instanceof DeliveryPartner){
                chosenDeliveryPartner = (DeliveryPartner) userRepo.getUserById(id);
                break;
            }
            failure("Invalid Delivery Partner Id.");
        }

        return chosenDeliveryPartner;
    }

    public FoodItem selectFoodItemFromCart(Order cart){
        if(cart == null){
            throw new EmptyCartException();
        }
        List<OrderItem> foodItems = cart.getItems();

        if(foodItems.isEmpty()){
            throw new EmptyCartException();
        }

        FoodItem chosenFoodItem = null;
        while(true) {
            info("Available Food Items In Cart: ");
            Display.displayOrderItemHeading();
            foodItems.forEach((i) -> success(i.toString()));
            info("Enter Food Item Id: ");
            int id = takeInt();
            chosenFoodItem = foodRepo.getFoodItemById(id);
            if(chosenFoodItem != null){
                break;
            }
            failure("Invalid Food Item Id.");
        }

        return chosenFoodItem;
    }
}
