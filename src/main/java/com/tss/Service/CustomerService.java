package com.tss.Service;


import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.OrderStatus;
import com.tss.Exception.CartContainsUnavailableItemsException;
import com.tss.Exception.EmptyCartException;
import com.tss.Exception.ItemNotAvailableException;
import com.tss.Exception.NoDataFoundException;
import com.tss.Payment.CashOnDelivery;
import com.tss.Payment.UPI;
import com.tss.Repository.FoodItemRepository;
import com.tss.Repository.UserRepository;
import com.tss.Utils.Display;
import com.tss.Utils.GlobalVariables;
import com.tss.model.*;
import com.tss.model.User.Customer;

import java.util.List;

import static com.tss.Utils.GlobalVariables.inputTaker;
import static com.tss.Utils.Input.takeInt;
import static com.tss.Utils.Print.*;

public class CustomerService {

    private final UserRepository userRepo;
    private final Customer customer;
    private final FoodItemRepository foodRepo;
    private final SelectorService selector;

    public CustomerService(){
        customer = (Customer) CurrentUser.getInstance().getUser();
        this.foodRepo = GlobalVariables.getInstance().foodItemRepository;
        this.userRepo = GlobalVariables.getInstance().userRepository;
        this.selector = new SelectorService();
    }

    private void addItemToCart(){
        FoodItem item = selector.selectFoodItem();
        if(item.getAvailability() == AvailabilityStatus.NOT_AVAILABLE){
            throw new ItemNotAvailableException();
        }

        info("Enter Quantity To Add: ");
        int quantity = takeInt();
        if(quantity == 0)
            throw new IllegalArgumentException("No Food Item Added.");
        customer.addItemToCart(item, quantity);
        success("Food Item added to cart !");
    }

    private void removeItemFromCart(){
        while(true) {
            FoodItem item = selector.selectFoodItemFromCart(customer.getCart());

            try {
                info("Enter Quantity To Remove: ");
                int quantity = takeInt();
                if(quantity == 0)
                    throw new IllegalArgumentException("No Food Item Removed.");
                boolean result = customer.removeItemFromCart(item, quantity);

                if (result) {
                    success("Food Item Successfully Removed !");
                    break;
                } else {
                    throw new IllegalArgumentException("Selected Food Item doesn't exist in cart.");
                }
            }
            catch(Exception e){
                exception(e);
            }
        }
    }

    private void placeOrder(){
        if(customer.getCart() == null){
            throw new EmptyCartException();
        }
        Order cart = customer.getCart();

        if(cart.getTotalAmount() == 0){
            throw new EmptyCartException();
        }

        for(OrderItem item: cart.getItems()){
            if( item.foodItem.getAvailability() == AvailabilityStatus.NOT_AVAILABLE ) {
                throw new CartContainsUnavailableItemsException();
            }
        };

        if(customer.getAddress().isEmpty() || customer.getAddress() == null){
            info("Enter Your Address: ");
            String address = inputTaker.nextLine();
            customer.setAddress(address);
        }

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
        success("You'll get notified once a delivery partner assigns to your order...");
    }

    private void performPayment(Order order){
        info("Order Invoice");
        Display.displayOrder(order);

        while(true){
            try {
                Display.displayPaymentMenu();

                int choice = takeInt();
                switch (choice) {
                    case 1 -> new UPI(order).pay();
                    case 2 -> new CashOnDelivery(order).pay();
                    case 3 -> {
                        //CANCELLED
                        order.moveToNextState(false);
                        success("Order Successfully Cancelled !");
                    }
                    default -> throw new IllegalArgumentException("Invalid Option Selected.");
                }
                break;
            }
            catch(Exception e){
                exception(e);
            }
        }

        //Confirmed -> Preparing
        order.moveToNextState(true);
    }

    private void showOrderHistory(){
        List<Order> orderHistory = customer.getOrderHistory();
        if(orderHistory.isEmpty()){
            throw new NoDataFoundException("Order History");
        }
        Display.displayOrderHeading();
        orderHistory.forEach((o) -> success(o.toString()));
    }

    public void showNotifications(){
        List<Notification> notifications = customer.getOldNotifications();
        List<Notification> newNotifications = customer.getNewNotifications();

        if(notifications.isEmpty() && newNotifications.isEmpty()){
            throw new NoDataFoundException("Notification History");
        }

        notifications.forEach(System.out::println);
        newNotifications.forEach((n)->success("*" + n.toString()));
    }

    private void showOnGoingOrders(){
        List<Order> orderHistory = customer.getOnGoingOrders();
        if(orderHistory.isEmpty()){
            throw new NoDataFoundException("On Going Orders");
        }
        Display.displayOrderHeading();
        orderHistory.forEach((o) -> success(o.toString()));
    }

    private void showCart(){
        if(customer.getCart() == null || customer.getCart().getTotalAmount() == 0){
            throw new EmptyCartException();
        }
        Display.displayOrder(customer.getCart());
    }

    private void unsubscribeFromNotifications() {
        GlobalVariables.getInstance()
                .customerNotificationChannel
                .unsubscribe(customer);

        success("Unsubscribed Successfully !");
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
                    case 4 -> showCart();
                    case 5 -> showOrderHistory();
                    case 6 -> showNotifications();
                    case 7 -> showOnGoingOrders();
                    case 8 -> unsubscribeFromNotifications();
                    case 0 -> {
                        success("<--Back");
                        return;
                    }
                    default -> throw new IllegalArgumentException("Invalid Option Selected.");
                }
            } catch (Exception e) {
                exception(e);
            }
        }
    }
}
