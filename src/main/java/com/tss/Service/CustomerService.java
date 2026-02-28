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
import com.tss.Repository.InMemoryFoodItemRepository;
import com.tss.Repository.InMemoryUserRepository;
import com.tss.Repository.UserRepository;
import com.tss.Utils.Display;
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
        success("Food Item added to cart !");
    }

    private void removeItemFromCart(){
        while(true) {
            FoodItem item = selector.selectFoodItemFromCart(customer.getCart());

            try {
                boolean result = customer.removeItemFromCart(item);

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

//    private void cancelOrder(){
//        customer.
//    }

    public void showOrderHistory(){
        List<Order> orderHistory = customer.getOrderHistory();
        if(orderHistory.isEmpty()){
            throw new NoDataFoundException("Order History");
        }
        Display.displayOrderHeading();
        orderHistory.forEach((o) -> success(o.toString()));
    }

    public void showNewNotifications(){
        List<Notification> notifications = customer.getNewNotifications();
        if(notifications.isEmpty()){
            throw new NoDataFoundException("New Notifications");
        }
        notifications.forEach((n) -> success(n.toString()));
    }

    public void showAllNotifications(){
        List<Notification> notifications = customer.getOldNotifications();
        notifications.addAll(customer.getNewNotifications());

        if(notifications.isEmpty()){
            throw new NoDataFoundException("Notification History");
        }
        notifications.forEach((n) -> success(n.toString()));
    }

    private void showOnGoingOrders(){
        List<Order> orderHistory = customer.getOnGoingOrders();
        if(orderHistory.isEmpty()){
            throw new NoDataFoundException("On Going Orders");
        }
        Display.displayOrderHeading();
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
                    case 4 -> Display.displayOrder(customer.getCart());
//                    case 5 -> failure("Operation Not Supported");
                    case 5 -> showOrderHistory();
                    case 6 -> showNewNotifications();
                    case 7 -> showAllNotifications();
                    case 8 -> showOnGoingOrders();
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
