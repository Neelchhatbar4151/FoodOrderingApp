package com.tss.model.User;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.Exception.DeliveryPartnerIsBusyException;
import com.tss.Observer.NotificationObserver;
import com.tss.Service.OrderService;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Notification;
import com.tss.model.Order;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPartner extends User implements NotificationObserver {

    private AvailabilityStatus status;
    public Order assignedOrder;
    private double totalEarnings;
    private final List<Order> deliveredOrders;
    private boolean isApproved;

    public static double commissionPercentage = 0.1;

    public DeliveryPartner(String name, String phone, String password) {
        super(name, phone, password, Role.DELIVERY_PARTNER);
        this.isApproved = false;
        this.status = AvailabilityStatus.AVAILABLE;
        this.assignedOrder = null;
        this.totalEarnings = 0.0;
        this.deliveredOrders = new ArrayList<>();

        addNotification(new Notification("You're Currently Not Approved as a Delivery partner, once Admin approves your account, you'll be eligible to deliver orders."));

        GlobalVariables.getInstance()
                .deliveryPartnerNotificationChannel
                .subscribe(this);
    }

    public void assignOrder(Order order) {
        if(status == AvailabilityStatus.NOT_AVAILABLE){
            throw new DeliveryPartnerIsBusyException();
        }
        status = AvailabilityStatus.NOT_AVAILABLE;
        assignedOrder = order;
    }

    public boolean completeDelivery(){
        if(assignedOrder == null){
            return false;
        }

        addNotification(new Notification("Order Completion, Commission Earned: " + (assignedOrder.getFinalAmount() * commissionPercentage) + ", Order Id: " + assignedOrder.getId()));
        assignedOrder.getCustomer().addNotification(new Notification("Order Delivered Successfully, Order Id: " + assignedOrder.getId()));
        assignedOrder.moveToNextState(true);
        this.deliveredOrders.add(assignedOrder);

        totalEarnings += (assignedOrder.getFinalAmount() * commissionPercentage);
        assignedOrder = null;
        status = AvailabilityStatus.AVAILABLE;

        if(isApproved){
            OrderService.getInstance().addDeliveryPartner(this);
        }

        return true;
    }

    public List<Order> getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setIsApproved(boolean state){
        if(state == isApproved){
            return;
        }
        if(state){
            isApproved = true;

            //Registering Delivery Partner in Queue
            if(status != AvailabilityStatus.NOT_AVAILABLE)
                OrderService.getInstance().addDeliveryPartner(this);
        }
        else{
            isApproved = false;
            OrderService.getInstance().removeDeliveryPartner(this);
        }
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public boolean getIsApproved(){
        return isApproved;
    }

    public Order getAssignedOrder() {
        return assignedOrder;
    }

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
