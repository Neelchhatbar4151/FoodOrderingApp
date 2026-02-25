package com.tss.model.User;

import com.tss.Datatype.Role;
import com.tss.model.FoodItem;
import com.tss.model.Order;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User{

    private final List<Order> orderList;
    private String upiId;
    private final Order cart;
    private String address;

    public Customer(String name, String phone, String password) {
        super(name, phone, password, Role.CUSTOMER);
        this.orderList = new ArrayList<>();
        this.upiId = "";
        this.cart = new Order(this);
        this.address = "";
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void addItemToCart(FoodItem item){
        cart.addItem(item);
    }

    public boolean removeItemFromCart(FoodItem item){
        return cart.removeItem(item);
    }

    public List<Order> getOrderHistory(){
        return new ArrayList<>(orderList);
    }

//    public Order getPastOrderById(int id){
//        return new Order();
//    }

    public void cancelOrder(int id){
        cart.moveToNextState(false);
    }

//    public List<Order> getOnGoingOrders(){
//        return list of Orders;
//    }


    @Override
    public String toString() {
        return "Customer{" +
                "orderList=" + orderList +
                ", upiId='" + upiId + '\'' +
                ", cart=" + cart +
                ", address='" + address + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", notifications=" + notifications +
                ", role=" + role +
                ", createdOn=" + createdOn +
                ", indexOfNewNotification=" + indexOfNewNotification +
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
}
