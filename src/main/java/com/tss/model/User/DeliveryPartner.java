package com.tss.model.User;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.Exception.DeliveryPartnerIsBusyException;
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
    }

    public void assignOrder(Order order) throws InterruptedException {
        if(status == AvailabilityStatus.NOT_AVAILABLE){
            throw new DeliveryPartnerIsBusyException();
        }
        status = AvailabilityStatus.NOT_AVAILABLE;
        assignedOrder = order;
        Thread.sleep(10000);
        order.moveToNextState(true);
        totalEarnings += (order.getFinalAmount() * commissionPercentage);
        assignedOrder = null;
        status = AvailabilityStatus.AVAILABLE;
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
