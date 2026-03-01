package com.tss.Service;


import com.tss.Exception.NoDataFoundException;
import com.tss.Utils.Display;
import com.tss.Utils.GlobalVariables;
import com.tss.model.CurrentUser;
import com.tss.model.Notification;
import com.tss.model.Order;
import com.tss.model.User.DeliveryPartner;

import java.util.List;

import static com.tss.Utils.Input.takeBoolean;
import static com.tss.Utils.Input.takeInt;
import static com.tss.Utils.Print.*;
import static com.tss.Utils.Print.exception;

public class DeliveryPartnerService {

    private final DeliveryPartner deliveryPartner;

    public DeliveryPartnerService(){
        deliveryPartner = (DeliveryPartner) CurrentUser.getInstance().getUser();
    }

    private void showDeliveredOrders(){
        List<Order> orderHistory = deliveryPartner.getDeliveredOrders();
        if(orderHistory.isEmpty()){
            throw new NoDataFoundException("Order History");
        }
        Display.displayOrderHeading();
        for(Order o: orderHistory){
            success(o.toString());
        }
    }

    private void showNotifications(){
        List<Notification> notifications = deliveryPartner.getOldNotifications();
        List<Notification> newNotifications = (deliveryPartner.getNewNotifications());

        if(notifications.isEmpty() && newNotifications.isEmpty()){
            throw new NoDataFoundException("Notification History");
        }

        notifications.forEach(System.out::println);
        newNotifications.forEach((n)->success("*" + n.toString()));
    }

    private void showAssignedOrder(){
        if(deliveryPartner.getAssignedOrder() == null){
            throw new IllegalStateException("No Order Assigned.");
        }

        Display.displayOrder(deliveryPartner.getAssignedOrder());
    }

    private void completeDelivery() {
        showAssignedOrder();

        info("Confirm ?(y/n): ");
        boolean confirm = takeBoolean();

        if(confirm){
            deliveryPartner.completeDelivery();
            success("Order Delivered Successfully !");
        }
    }

    private void getTotalEarnings(){
        success("Your Total Earnings: " + deliveryPartner.getTotalEarnings());
    }

    private void unsubscribeFromNotifications() {
        GlobalVariables.getInstance()
                .deliveryPartnerNotificationChannel
                .unsubscribe(deliveryPartner);
        success("Unsubscribed Successfully !");
    }

    public void start(){
        while(true){
            try {
                Display.displayDeliveryPartnerMenu();

                int choice = takeInt();
                switch (choice) {
                    case 1 -> completeDelivery();
                    case 2 -> showAssignedOrder();
                    case 3 -> getTotalEarnings();
                    case 4 -> showDeliveredOrders();
                    case 5 -> showNotifications();
                    case 6 -> unsubscribeFromNotifications();
                    case 0 -> {
                        success("<--Back");
                        return;
                    }
                    default -> throw new IllegalArgumentException("Invalid Choice.");
                }
            } catch (Exception e) {
                exception(e);
            }
        }
    }


}
