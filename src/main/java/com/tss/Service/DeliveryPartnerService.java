package com.tss.Service;


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
        deliveryPartner.completeDelivery();

        success("Order Delivered Successfully !");
    }

    private void getTotalEarnings(){
        success("Your Total Earnings: " + deliveryPartner.getTotalEarnings());
    }

    public void start(){
        while(true){
            try {
                info("""
                    ================== CUSTOMER MENU ==================
                    1. Set Availability Status (NS)
                    2. Complete Order Delivery
                    3. Get Total Earnings
                    4. Show Orders Delivered
                    5. Show New Notifications
                    6. Show All Notifications
                    0.  Go Back
                    ================================================
                    Enter your choice:""");

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
                    default -> failure("Invalid choice.");
                }
            } catch (Exception e) {
                exception(e);
            }
        }
    }


}
