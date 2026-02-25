package com.tss.Service;


import com.tss.Repository.FoodItemRepository;
import com.tss.Repository.InMemoryFoodItemRepository;
import com.tss.Repository.UserRepository;
import com.tss.model.CurrentUser;
import com.tss.model.FoodItem;
import com.tss.model.User.Customer;

import java.nio.channels.Selector;

import static com.tss.Utils.Print.*;

public class CustomerService {

    private final UserRepository userRepo;
    private final Customer customer;
    private final FoodItemRepository foodRepo;
    private final SelectorService selector;

    public CustomerService(UserRepository userRepo){
        customer = (Customer) CurrentUser.getInstance().getUser();
        this.foodRepo = new InMemoryFoodItemRepository();
        this.userRepo = userRepo;
        this.selector = new SelectorService(userRepo, foodRepo);
    }

    private void addItemToCart(){
        FoodItem item = selector.selectFoodItem();
        customer.addItemToCart(item);
    }

    private void removeItemFromCart(){
        FoodItem item = selector.selectFoodItem();

        boolean result = customer.removeItemFromCart(item);

        if(result){
            success("Food Item Successfully Removed !");
        }
        else{
            failure("Food Item doesn't exist in Cart.");
        }
    }

    private void placeOrder(){
        customer.getCart().moveToNextState(true);

        customer.getCart().moveToNextState(true);

        customer.getCart().moveToNextState(true);
    }

//    private void cancelOrder(){
//        customer.
//    }



    public void start(){

    }
}
