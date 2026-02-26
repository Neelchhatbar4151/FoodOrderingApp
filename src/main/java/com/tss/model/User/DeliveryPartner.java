package com.tss.model.User;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.Exception.DeliveryPartnerIsBusyException;
import com.tss.Service.OrderService;
import com.tss.model.Notification;
import com.tss.model.Order;

import java.util.ArrayList;
import java.util.List;

import static com.tss.Utils.Constant.commissionPercentage;

public class DeliveryPartner extends User {

    private AvailabilityStatus status;
    public Order assignedOrder;
    private double totalEarnings;
    private final List<Order> deliveredOrders;
    private boolean isApproved;

    public DeliveryPartner(String name, String phone, String password) {
        super(name, phone, password, Role.DELIVERY_PARTNER);
        this.isApproved = false;
        this.status = AvailabilityStatus.AVAILABLE;
        this.assignedOrder = null;
        this.totalEarnings = 0.0;
        this.deliveredOrders = new ArrayList<>();

        addNotification(new Notification("You're Currently Not Approved as a Delivery partner, once Admin approves your account, you'll be eligible to deliver orders."));
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

        addNotification(new Notification("Order Completion, Commission Earned: " + (assignedOrder.getFinalAmount() * commissionPercentage) + ", Order: " + assignedOrder));
        assignedOrder.getCustomer().addNotification(new Notification("Order Delivered Successfully, Order: " + assignedOrder));
        assignedOrder.moveToNextState(true);

        totalEarnings += (assignedOrder.getFinalAmount() * commissionPercentage);
        assignedOrder = null;
        status = AvailabilityStatus.AVAILABLE;

        OrderService.getInstance().addDeliveryPartner(this);

        return true;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public List<Order> getDeliveredOrders() {
        return deliveredOrders;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean state){
        if(state){
            isApproved = true;
            //Registering Delivery Partner in Queue
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

    @Override
    public String toString() {
        return String.format(
                "%-25s %-15s %-15s %-12.2f %-20s %-20s %-12s",
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
