package com.tss.model.User;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.Exception.DeliveryPartnerIsBusyException;
import com.tss.Observer.NotificationObserver;
import com.tss.Service.OrderService;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Notification;
import com.tss.model.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPartner extends User {

    private boolean isAvailable;
    private double totalEarnings;
    private boolean isApproved;
    private long priority;

    public DeliveryPartner(long id, String name, String phone, String password, long seenNotificationCount, LocalDateTime createdOn, Boolean isAvailable, Boolean isApproved, double totalEarnings) {
        super(id, name, phone, password, seenNotificationCount, createdOn);
        this.isApproved = isApproved;
        this.isAvailable = isAvailable;
        this.totalEarnings = totalEarnings;

        addNotification(new Notification("You're Currently Not Approved as a Delivery partner, once Admin approves your account, you'll be eligible to deliver orders."));
    }
//
//    public void assignOrder(Order order) {
//        if(status == AvailabilityStatus.NOT_AVAILABLE){
//            throw new DeliveryPartnerIsBusyException();
//        }
//        status = AvailabilityStatus.NOT_AVAILABLE;
//        assignedOrder = order;
//    }
//
//    public boolean completeDelivery(){
//        if(assignedOrder == null){
//            return false;
//        }
//
//        addNotification(new Notification("Order Completion, Commission Earned: " + (assignedOrder.getFinalAmount() * commissionPercentage) + ", Order Id: " + assignedOrder.getId()));
//        assignedOrder.getCustomer().addNotification(new Notification("Order Delivered Successfully, Order Id: " + assignedOrder.getId()));
//        assignedOrder.moveToNextState(true);
//        this.deliveredOrders.add(assignedOrder);
//
//        totalEarnings += (assignedOrder.getFinalAmount() * commissionPercentage);
//        assignedOrder = null;
//        status = AvailabilityStatus.AVAILABLE;
//
//        if(isApproved){
//            OrderService.getInstance().addDeliveryPartner(this);
//        }
//
//        return true;
//    }
//
//    public List<Order> getDeliveredOrders() {
//        return deliveredOrders;
//    }
//
//    public void setIsApproved(boolean state){
//        if(state == isApproved){
//            return;
//        }
//        if(state){
//            isApproved = true;
//
//            //Registering Delivery Partner in Queue
//            if(status != AvailabilityStatus.NOT_AVAILABLE)
//                OrderService.getInstance().addDeliveryPartner(this);
//        }
//        else{
//            isApproved = false;
//            OrderService.getInstance().removeDeliveryPartner(this);
//        }
//    }
//
//    public double getTotalEarnings() {
//        return totalEarnings;
//    }
//
//    public boolean getIsApproved(){
//        return isApproved;
//    }

    @Override
    public String toString() {
        return String.format(
                "%5s %-20s %-15s %-15s %-12.2f %-20s %-20s %-12s",
                id,
                role,
                phone,
                name,
                totalEarnings,
                (isApproved?"Approved":"Not Approved"),
                createdOn,
                status
        );
    }
}
