package com.tss.Service;


import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.OrderStatus;
import com.tss.Exception.CartContainsUnavailableItemsException;
import com.tss.Exception.EmptyCartException;
import com.tss.Exception.ItemNotAvailableException;
import com.tss.Repository.FoodItemRepository;
import com.tss.Repository.InMemoryFoodItemRepository;
import com.tss.Repository.InMemoryUserRepository;
import com.tss.Repository.UserRepository;
import com.tss.Utils.Display;
import com.tss.model.*;
import com.tss.model.User.Customer;

import java.nio.channels.Selector;
import java.util.List;

import static com.tss.Utils.Input.takeInt;
import static com.tss.Utils.Print.*;

public class CustomerService {

    private final UserRepository userRepo;
    private final Customer customer;
    private final FoodItemRepository foodRepo;
    private final SelectorService selector;

    public CustomerService(){
        customer = (Customer) CurrentUser.getInstance().getUser();
        this.foodRepo = InMemoryFoodItemRepository.getInstance();
        this.userRepo = InMemoryUserRepository.getInstance();
        this.selector = new SelectorService();
    }

    private void addItemToCart(){
        FoodItem item = selector.selectFoodItem();
        if(item.getAvailability() == AvailabilityStatus.NOT_AVAILABLE){
            throw new ItemNotAvailableException();
        }
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
        Order cart = customer.getCart();

        if(cart.getTotalAmount() == 0){
            throw new EmptyCartException();
        }

        cart.getItems()
                .forEach((item)-> {

                    if( item.foodItem.getAvailability() == AvailabilityStatus.NOT_AVAILABLE ) {
                        throw new CartContainsUnavailableItemsException();
                    }

                });

        customer.setNewCart();
        //Created -> Confirmed
        cart.moveToNextState(true);

        performPayment(cart);

        if(cart.getStatus() == OrderStatus.CANCELLED){
            return ;
        }

        //No Preparation Time For Now
        //Preparing -> Waiting
        cart.moveToNextState(true);

        OrderService.getInstance().addOrder(cart);
        success("Your Order is Now in Queue...");
        success("You'll get notified once a delivery partner assigns to your order.");
    }

    private void performPayment(Order order){
        info("Order Invoice");
        success(order.toString());

        while(true){
            Display.displayPaymentMenu();

            int choice = takeInt();
            switch(choice){
                case 1 -> success("Paid Using UPI");
                case 2 -> success("Delivery Partner will take payment upon Delivery.");
                case 3 -> {
                    //CANCELLED
                    order.moveToNextState(false);
                    success("Order Successfully Cancelled !");
                }
                default -> {
                    failure("Enter Valid Choice.");
                    continue;
                }
            }
            break;
        }

        //Confirmed -> Preparing
        order.moveToNextState(true);
    }

//    private void cancelOrder(){
//        customer.
//    }

    public void showOrderHistory(){
        List<Order> orderHistory = customer.getOrderHistory();
        if(orderHistory.isEmpty()){
            failure("No Order History.");
        }
        orderHistory.forEach((o) -> success(o.toString()));
    }

    public void showNewNotifications(){
        List<Notification> notifications = customer.getNewNotifications();
        if(notifications.isEmpty()){
            failure("No Notification History.");
        }
        notifications.forEach((n) -> success(n.toString()));
    }

    public void showAllNotifications(){
        List<Notification> notifications = customer.getOldNotifications();
        notifications.addAll(customer.getNewNotifications());

        if(notifications.isEmpty()){
            failure("No Notification History.");
        }
        notifications.forEach((n) -> success(n.toString()));
    }

    private void showOnGoingOrders(){
        List<Order> orderHistory = customer.getOnGoingOrders();
        if(orderHistory.isEmpty()){
            failure("No On Going Order.");
        }
        orderHistory.forEach((o) -> success(o.toString()));
    }

    public void start(){
        while(true){
            try {
                Display.displayCustomerMenu();

                int choice = takeInt();
                switch (choice) {
                    case 1 -> addItemToCart();
                    case 2 -> removeItemFromCart();
                    case 3 -> placeOrder();
                    case 4 -> success(customer.getCart().toString());
                    case 5 -> failure("Operation Not Supported");
                    case 6 -> showOrderHistory();
                    case 7 -> showNewNotifications();
                    case 8 -> showAllNotifications();
                    case 9 -> showOnGoingOrders();
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
