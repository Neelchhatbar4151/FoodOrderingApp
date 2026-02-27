package com.tss.model.User;

import com.tss.Datatype.OrderStatus;
import com.tss.Datatype.Role;
import com.tss.model.FoodItem;
import com.tss.model.Order;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User{

    private final List<Order> orderList;
    private String upiId;
    private Order cart;
    private String address;

    public Customer(String name, String phone, String password) {
        super(name, phone, password, Role.CUSTOMER);
        this.orderList = new ArrayList<>();
        this.upiId = "";
        this.cart = new Order(this);
        this.address = "";
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
}
