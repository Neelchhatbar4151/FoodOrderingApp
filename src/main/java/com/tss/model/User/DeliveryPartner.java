package com.tss.model.User;

import com.tss.DB.DBConnection;
import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.Exception.DeliveryPartnerIsBusyException;
import com.tss.Observer.NotificationObserver;
import com.tss.Service.OrderService;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Notification;
import com.tss.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPartner extends User implements NotificationObserver {

    private AvailabilityStatus status;
    public Order assignedOrder;
    private double totalEarnings;
    private List<Order> deliveredOrders;
    private boolean isApproved;

    public static double commissionPercentage = 0.1;

    public DeliveryPartner(String name, String phone, String password) {
        super(name, phone, password, Role.DELIVERY_PARTNER);
        this.isApproved = false;
        this.status = AvailabilityStatus.AVAILABLE;
        this.assignedOrder = null;
        this.totalEarnings = 0.0;
        this.deliveredOrders = new ArrayList<>();

        addNotification(new Notification(
                "You're Currently Not Approved as a Delivery partner, once Admin approves your account, you'll be eligible to deliver orders."
        ));

        GlobalVariables.getInstance()
                .deliveryPartnerNotificationChannel
                .subscribe(this);
    }

    private void persistAssignmentAvailability(boolean isAvailable) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE delivery_partner SET is_available = ? WHERE delivery_partner_id = ?");
            ps.setBoolean(1, isAvailable);
            ps.setLong(2, this.id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to update delivery partner availability.");
        }
    }

    private void persistCompletion() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    UPDATE delivery_partner
                    SET total_earnings = ?,
                        is_available = true
                    WHERE delivery_partner_id = ?
                    """);
            ps.setDouble(1, totalEarnings);
            ps.setLong(2, this.id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to persist delivery completion.");
        }
    }


    private void persistApproval(boolean approved) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE delivery_partner SET is_approved = ? WHERE delivery_partner_id = ?");
            ps.setBoolean(1, approved);
            ps.setLong(2, this.id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to update delivery partner approval state.");
        }
    }

    private static double getCommissionRateFromDb() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT value FROM app_config WHERE key = 'commission_percentage'");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double percent = Double.parseDouble(rs.getString("value"));
                if (percent < 0 || percent > 100) {
                    throw new IllegalStateException("commission_percentage in app_config must be between 0 and 100.");
                }
                return percent / 100.0;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read commission percentage from app_config.");
        }

        return commissionPercentage;
    }

    public void setAssignedOrderFromDb(Order order) {
        this.assignedOrder = order;
        this.status = (order == null) ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.NOT_AVAILABLE;
    }

    public void assignOrder(Order order) {
        if(status == AvailabilityStatus.NOT_AVAILABLE){
            throw new DeliveryPartnerIsBusyException();
        }
        status = AvailabilityStatus.NOT_AVAILABLE;
        assignedOrder = order;
        persistAssignmentAvailability(false);
    }

    public boolean completeDelivery(){
        if(assignedOrder == null){
            return false;
        }

        double currentCommissionRate = getCommissionRateFromDb();

        addNotification(new Notification(
                "Order Completion, Commission Earned: " +
                        (assignedOrder.getFinalAmount() * currentCommissionRate) +
                        ", Order Id: " + assignedOrder.getId()
        ));

        assignedOrder.getCustomer().addNotification(
                new Notification("Order Delivered Successfully, Order Id: " + assignedOrder.getId())
        );

        assignedOrder.moveToNextState(true);
        this.deliveredOrders.add(assignedOrder);

        totalEarnings += (assignedOrder.getFinalAmount() * currentCommissionRate);
        assignedOrder = null;
        status = AvailabilityStatus.AVAILABLE;
        persistCompletion();

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
            persistApproval(true);
            if(status != AvailabilityStatus.NOT_AVAILABLE)
                OrderService.getInstance().addDeliveryPartner(this);
        } else {
            isApproved = false;
            persistApproval(false);
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

    public static class Builder {

        private long id;
        private String name;
        private String phone;
        private String password;
        private List<Notification> notifications;
        private int indexOfNewNotification;
        private Role role;
        private LocalDateTime createdOn;

        private AvailabilityStatus status;
        private Order assignedOrder;
        private double totalEarnings;
        private List<Order> deliveredOrders;
        private boolean isApproved;

        public Builder setId(long id) { this.id = id; return this; }
        public Builder setName(String name) { this.name = name; return this; }
        public Builder setPhone(String phone) { this.phone = phone; return this; }
        public Builder setPassword(String password) { this.password = password; return this; }
        public Builder setNotifications(List<Notification> notifications) { this.notifications = notifications; return this; }
        public Builder setIndexOfNewNotification(int index) { this.indexOfNewNotification = index; return this; }
        public Builder setRole(Role role) { this.role = role; return this; }
        public Builder setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; return this; }

        public Builder setStatus(AvailabilityStatus status) { this.status = status; return this; }
        public Builder setAssignedOrder(Order assignedOrder) { this.assignedOrder = assignedOrder; return this; }
        public Builder setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; return this; }
        public Builder setDeliveredOrders(List<Order> deliveredOrders) { this.deliveredOrders = deliveredOrders; return this; }
        public Builder setIsApproved(boolean isApproved) { this.isApproved = isApproved; return this; }

        public DeliveryPartner build() {
            DeliveryPartner d = new DeliveryPartner(name, phone, password);

            d.id = id;
            d.name = name;
            d.phone = phone;
            d.password = password;
            d.notifications = notifications;
            d.indexOfNewNotification = indexOfNewNotification;
            d.role = role;
            d.createdOn = createdOn;

            d.status = status;
            d.assignedOrder = assignedOrder;
            d.totalEarnings = totalEarnings;
            d.deliveredOrders = (deliveredOrders != null) ? deliveredOrders : new ArrayList<>();
            d.isApproved = isApproved;
            d.addNotification(new Notification(
                    "You're Currently Not Approved as a Delivery partner, once Admin approves your account, you'll be eligible to deliver orders."
            ));
            return d;
        }
    }
}
