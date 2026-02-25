package com.tss.model;

import com.tss.Datatype.OrderStatus;
import com.tss.model.User.DeliveryPartner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tss.Utils.Constant.newOrderId;

public class Order {
    private final int id;
    private final List<OrderItem> items;
    private double totalAmount;
    private double discount;
    private String discountDescription;
    private double finalAmount;
//    private final PaymentService paymentService;
    private final String transactionId;
    private DeliveryPartner deliveryPartner;
    private OrderStatus status;
    private final LocalDate orderPlacedOn;

    private final static Map<OrderStatus, OrderStatus> movementGraph = new HashMap<>();

    static {
        movementGraph.put(OrderStatus.CREATED, OrderStatus.CONFIRMED);
        movementGraph.put(OrderStatus.CONFIRMED, OrderStatus.PREPARING);
        movementGraph.put(OrderStatus.PREPARING, OrderStatus.OUT_FOR_DELIVERY);
        movementGraph.put(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED);
        movementGraph.put(OrderStatus.DELIVERED, null);
        movementGraph.put(OrderStatus.CANCELLED, null);
    }

    public Order(){
        this.id = newOrderId++;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
        this.discount = 0.0;
        this.discountDescription = null;
        this.finalAmount = 0.0;
//        this.paymentService = null;
        this.transactionId = null;
        this.deliveryPartner = null;
        this.status = OrderStatus.CREATED;
        this.orderPlacedOn = null;
    }

    public boolean moveToNextState(boolean flag){
        if(!flag){
            status = OrderStatus.CANCELLED;
        }

        return (status = movementGraph.get(status)) != null;
    }

    public List<OrderItem> getItemsListCopy(){
        return new ArrayList<>(items);
    }

    public boolean isOrderPlaced(){
        return ((status != OrderStatus.CANCELLED) && (status != OrderStatus.CREATED));
    }

    public void addItem(FoodItem item){
        for(OrderItem orderItem: items){
            if(orderItem.foodItem == item){
                orderItem.increaseQuantity();
                return;
            }
        }
        items.add(new OrderItem(item));
        totalAmount += item.price;
        finalAmount = totalAmount - discount;
    }

    public void assignDeliveryPartner(DeliveryPartner deliveryPartner){
        this.deliveryPartner = deliveryPartner;
    }

    public void setDiscount(double discount){
        this.discount = discount;
        finalAmount = totalAmount - discount;
    }

    public void setDiscountDescription(String description){
        this.discountDescription = discountDescription;
    }

    public int getId() {
        return id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public DeliveryPartner getDeliveryPartner() {
        return deliveryPartner;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDate getOrderPlacedOn() {
        return orderPlacedOn;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", items=" + items +
                ", totalAmount=" + totalAmount +
                ", discount=" + discount +
                ", discountDescription='" + discountDescription + '\'' +
                ", finalAmount=" + finalAmount +
//                ", paymentService=" + paymentService +
                ", transactionId='" + transactionId + '\'' +
                ", deliveryPartner=" + deliveryPartner +
                ", status=" + status +
                ", orderPlacedOn=" + orderPlacedOn +
                '}';
    }
}
