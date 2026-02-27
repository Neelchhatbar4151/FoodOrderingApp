package com.tss.model;

import com.tss.Datatype.OrderStatus;
import com.tss.model.User.Customer;
import com.tss.model.User.DeliveryPartner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tss.Utils.Constant.newOrderId;

public class Order {
    private final int id;
    private final List<OrderItem> items;
    private String discountDescription;
    private String paymentService;
    private String transactionId;
    private DeliveryPartner deliveryPartner;
    private OrderStatus status;
    private LocalDateTime orderPlacedOn;
    private final Customer customer;

    private final static Map<OrderStatus, OrderStatus> movementGraph = new HashMap<>();

    static {
        movementGraph.put(OrderStatus.CREATED, OrderStatus.CONFIRMED);
        movementGraph.put(OrderStatus.CONFIRMED, OrderStatus.PREPARING);
        movementGraph.put(OrderStatus.PREPARING, OrderStatus.WAITING_FOR_DELIVERY_PARTNER);
        movementGraph.put(OrderStatus.WAITING_FOR_DELIVERY_PARTNER, OrderStatus.OUT_FOR_DELIVERY);
        movementGraph.put(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED);
        movementGraph.put(OrderStatus.DELIVERED, null);
        movementGraph.put(OrderStatus.CANCELLED, null);
    }

    public Order(Customer customer){
        this.id = newOrderId++;
        this.items = new ArrayList<>();
        this.discountDescription = null;
        this.paymentService = null;
        this.transactionId = null;
        this.deliveryPartner = null;
        this.status = OrderStatus.CREATED;
        this.orderPlacedOn = null;
        this.customer = customer;
    }

    public boolean moveToNextState(boolean flag){
        if(!flag){
            status = OrderStatus.CANCELLED;
        }
        if(status == OrderStatus.CREATED){
            orderPlacedOn = LocalDateTime.now();
        }
        OrderStatus tempStatus = movementGraph.get(status);

        if(tempStatus == null){
            return false;
        }

        status = tempStatus;

        return true;
    }

    public void addItem(FoodItem item){
        for(OrderItem orderItem: items){
            if(orderItem.foodItem == item){
                orderItem.increaseQuantity();
                return;
            }
        }
        items.add(new OrderItem(item));
    }

    public boolean removeItem(FoodItem item){
        boolean result = true;
        OrderItem tempOrderItem = null;
        for(OrderItem orderItem: items){
            if(orderItem.foodItem == item){
                result = orderItem.decreaseQuantity();
                tempOrderItem = orderItem;
                break;
            }
        }

        if(tempOrderItem == null){
            return false;
        }

        if(!result){
            items.remove(tempOrderItem);
        }

        return true;
    }

    public void assignDeliveryPartner(DeliveryPartner deliveryPartner){
        this.deliveryPartner = deliveryPartner;
    }

    public int getId() {
        return id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        double total = 0.0;
        for(OrderItem item : items){
            total += item.getSubTotal();
        }
        return total;
    }

    public double getFinalAmount() {
        return getTotalAmount() - getDiscount();
    }

    public DeliveryPartner getDeliveryPartner() {
        return deliveryPartner;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getOrderPlacedOn() {
        return orderPlacedOn;
    }

    public void setPaymentService(String service){
        paymentService = service;
    }

    public void setTransactionId(String transactionId){
        this.transactionId = transactionId;
    }

    public Customer getCustomer(){
        return customer;
    }

    public double getDiscount(){
        if(getTotalAmount() > 500){
            discountDescription = "Flat 50 off on orders greater than 500.";
            return 50;
        }
        discountDescription = null;
        return 0;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return String.format(
                "%-8d %-15s %-18s %-25s %-12.2f %-10.2f %-12.2f %-30s %-20s %-25s",
                id,
                (paymentService == null?"-":paymentService),
                (transactionId == null?"-":transactionId),
                (deliveryPartner != null?
                deliveryPartner.getName() + " (" + deliveryPartner.getPhone() + ")":
                "Not Assigned Yet"),
                getTotalAmount(),
                getDiscount(),
                getFinalAmount(),
                status,
                (orderPlacedOn == null?"-":orderPlacedOn.format(formatter)),
                (discountDescription == null?"-":discountDescription)
        );
    }

    public String getDiscountDescription() {
        getDiscount();
        return discountDescription;
    }
}
