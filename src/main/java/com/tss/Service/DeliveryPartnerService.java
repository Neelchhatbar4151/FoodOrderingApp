package com.tss.Service;


import com.tss.Utils.Display;
import com.tss.model.CurrentUser;
import com.tss.model.Notification;
import com.tss.model.Order;
import com.tss.model.User.DeliveryPartner;

import java.util.List;

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
            failure("No Order History.");
        }
        Display.displayOrderHeading();
        for(Order o: orderHistory){
            success(o.toString());
        }
    }

    private void showNewNotifications(){
        List<Notification> notifications = deliveryPartner.getNewNotifications();
        if(notifications.isEmpty()){
            failure("No Notification History.");
        }
        for(Notification n: notifications){
            success(n.toString());
        }
    }

    private void showAllNotifications(){
        List<Notification> notifications = deliveryPartner.getOldNotifications();
        notifications.addAll(deliveryPartner.getNewNotifications());

        if(notifications.isEmpty()){
            failure("No Notification History.");
        }
        for(Notification n: notifications){
            success(n.toString());
        }
    }

    private void completeDelivery() {
        boolean result = deliveryPartner.completeDelivery();

        if(!result){
            failure("Order is Not assigned Yet !");
            return ;
        }

        success("Order Delivered Successfully !");
    }

    private void getTotalEarnings(){
        success("Your Total Earnings: " + deliveryPartner.getTotalEarnings());
    }

    public void start(){
        while(true){
            try {
                Display.displayDeliveryPartnerMenu();

                int choice = takeInt();
                switch (choice) {
                    case 1 -> failure("Operation not supported.");
                    case 2 -> completeDelivery();
                    case 3 -> getTotalEarnings();
                    case 4 -> showDeliveredOrders();
                    case 5 -> showNewNotifications();
                    case 6 -> showAllNotifications();
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
