package com.tss.Service;

import com.tss.Datatype.Role;
import com.tss.Repository.FoodItemRepository;
import com.tss.Repository.InMemoryFoodItemRepository;
import com.tss.Repository.UserRepository;
import com.tss.Utils.Display;
import com.tss.model.Category;
import com.tss.model.FoodItem;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

import java.util.Collections;
import java.util.List;

import static com.tss.Utils.Input.takeInt;
import static com.tss.Utils.Print.*;
import static com.tss.Utils.Print.success;

public class SelectorService {

    private final UserRepository userRepo;
    private final FoodItemRepository foodRepo;

    public SelectorService(UserRepository userRepo, FoodItemRepository foodRepo){
        this.userRepo = userRepo;
        this.foodRepo = foodRepo;
    }

    public Category selectFoodCategory(){
        List<Category> categories = foodRepo.getAllFoodCategories();

        if(categories.isEmpty()){
            failure("No Category Exists.");
            return null;
        }

        Category chosenCategory = null;
        while(true) {
            info("Available Food Categories: ");
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
            failure("No Food Item Exists.");
            return null;
        }

        FoodItem chosenFoodItem = null;
        while(true) {
            info("Available Food Items: ");
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
            failure("No Delivery Partner Exists.");
            return null;
        }

        DeliveryPartner chosenDeliveryPartner = null;
        while(true) {
            info("Available Delivery Partners: ");
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
}
