package com.tss.model.User;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.Exception.DeliveryPartnerIsBusyException;
import com.tss.Service.OrderService;
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

        //Registering Delivery Partner in Queue
        OrderService.getInstance().addDeliveryPartner(this);
    }

    public void assignOrder(Order order) {
        if(status == AvailabilityStatus.NOT_AVAILABLE){
            throw new DeliveryPartnerIsBusyException();
        }
        status = AvailabilityStatus.NOT_AVAILABLE;
        assignedOrder = order;
    }

    public void completeDelivery(){
        if(assignedOrder == null){
            return ;
        }

        totalEarnings += (assignedOrder.getFinalAmount() * commissionPercentage);
        assignedOrder = null;
        status = AvailabilityStatus.AVAILABLE;

        OrderService.getInstance().addDeliveryPartner(this);
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
        this.isApproved = state;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    @Override
    public String toString() {
        return "DeliveryPartner{" +
                "status=" + status +
                ", assignedOrder=" + assignedOrder +
                ", totalEarnings=" + totalEarnings +
                ", isApproved=" + isApproved +
                ", deliveredOrders=" + deliveredOrders +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", notifications=" + notifications +
                ", indexOfNewNotification=" + indexOfNewNotification +
                ", role=" + role +
                ", createdOn=" + createdOn +
                '}';
    }
}
