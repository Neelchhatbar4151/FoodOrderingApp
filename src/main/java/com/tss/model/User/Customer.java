package com.tss.model.User;

import com.tss.Datatype.OrderStatus;
import com.tss.Datatype.Role;
import com.tss.Observer.NotificationObserver;
import com.tss.Utils.GlobalVariables;
import com.tss.model.FoodItem;
import com.tss.model.Notification;
import com.tss.model.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User implements NotificationObserver {

    private List<Order> orderList;
    private String upiId;
    private Order cart;
    private String address;

    public Customer(String name, String phone, String password) {
        super(name, phone, password, Role.CUSTOMER);
        this.orderList = new ArrayList<>();
        this.upiId = "";
        this.cart = new Order(this);
        this.address = "";
        GlobalVariables.getInstance()
                .customerNotificationChannel
                .subscribe(this);
    }

    public void addItemToCart(FoodItem item, int quantity){
        cart.addItem(item, quantity);
    }

    public boolean removeItemFromCart(FoodItem item, int quantity){
        return cart.removeItem(item, quantity);
    }

    public List<Order> getOrderHistory(){
        return new ArrayList<>(orderList);
    }

    public void setNewCart(){
        orderList.add(cart);
        cart = new Order(this);
    }

    public List<Order> getOnGoingOrders(){
        List<Order> onGoingOrders = new ArrayList<>();
        for(int i=orderList.size()-1;i>=0;i--){
            if(orderList.get(i).getStatus() != OrderStatus.CANCELLED && orderList.get(i).getStatus() != OrderStatus.DELIVERED){
                onGoingOrders.add(orderList.get(i));
            }
        }
        return onGoingOrders;
    }

    @Override
    public String toString() {
        return "Customer{" +
                ", upiId='" + upiId + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", createdOn=" + createdOn +
                '}';
    }

    //Only Getters, Setters Below.
    public String getUpiId() {
        return upiId;
    }

    public Order getCart() {
        return cart;
    }

    public String getAddress() {
        return address;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    // ✅ FULL BUILDER
    public static class Builder {

        // User fields
        private long id;
        private String name;
        private String phone;
        private String password;
        private List<Notification> notifications;
        private int indexOfNewNotification;
        private Role role;
        private LocalDateTime createdOn;

        // Customer fields
        private List<Order> orderList;
        private String upiId;
        private Order cart;
        private String address;

        public Builder setId(long id) { this.id = id; return this; }
        public Builder setName(String name) { this.name = name; return this; }
        public Builder setPhone(String phone) { this.phone = phone; return this; }
        public Builder setPassword(String password) { this.password = password; return this; }
        public Builder setNotifications(List<Notification> notifications) { this.notifications = notifications; return this; }
        public Builder setIndexOfNewNotification(int index) { this.indexOfNewNotification = index; return this; }
        public Builder setRole(Role role) { this.role = role; return this; }
        public Builder setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; return this; }

        public Builder setOrderList(List<Order> orderList) { this.orderList = orderList; return this; }
        public Builder setUpiId(String upiId) { this.upiId = upiId; return this; }
        public Builder setCart(Order cart) { this.cart = cart; return this; }
        public Builder setAddress(String address) { this.address = address; return this; }

        public Customer build() {
            Customer c = new Customer(name, phone, password);

            c.id = id;
            c.name = name;
            c.phone = phone;
            c.password = password;
            c.notifications = notifications;
            c.indexOfNewNotification = indexOfNewNotification;
            c.role = role;
            c.createdOn = createdOn;

            c.orderList = (orderList != null) ? orderList : new ArrayList<>();
            c.upiId = upiId;
            c.cart = cart;
            c.address = address;

            return c;
        }
    }

}
