package com.tss.model;

import com.tss.Datatype.OrderStatus;
import com.tss.Payment.PaymentMode;
import com.tss.Repository.OrderItemRepository;
import com.tss.Repository.OrderRepository;
import com.tss.Repository.PaymentRepository;
import com.tss.Utils.GlobalVariables;
import com.tss.model.User.Customer;
import com.tss.model.User.DeliveryPartner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Order {
    private long id;
    private List<OrderItem> items;
    private String discountDescription;
    private PaymentMode payment;
    private DeliveryPartner deliveryPartner;
    private OrderStatus status;
    private LocalDateTime orderPlacedOn;
    private Customer customer;

    private final static Map<OrderStatus, OrderStatus> movementGraph = new HashMap<>();

    private static final OrderRepository orderRepository = GlobalVariables.getInstance().orderRepository;
    private static final OrderItemRepository orderItemRepository = GlobalVariables.getInstance().orderItemRepository;
    private static final PaymentRepository paymentRepository = GlobalVariables.getInstance().paymentRepository;

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
        this.id = GlobalVariables.getInstance().newOrderId++;
        this.items = new ArrayList<>();
        this.discountDescription = null;
        this.payment = null;
        this.deliveryPartner = null;
        this.status = OrderStatus.CREATED;
        this.orderPlacedOn = null;
        this.customer = customer;
    }

    public boolean moveToNextState(boolean flag){
        if(!flag){
            status = OrderStatus.CANCELLED;
            return orderRepository.updateStatus(id, status);
        }
        if(status == OrderStatus.CREATED){
            orderPlacedOn = LocalDateTime.now();
            orderRepository.updatePlacedOn(id, orderPlacedOn);
        }
        OrderStatus tempStatus = movementGraph.get(status);

        if(tempStatus == null){
            return false;
        }

        status = tempStatus;
        return orderRepository.updateStatus(id, status);
    }

    public void addItem(FoodItem item, int quantity){
        for(OrderItem orderItem: items){
            if(orderItem.foodItem.id == item.id){
                orderItem.increaseQuantity(quantity);
                orderItemRepository.updateQuantity(orderItem.id, orderItem.getCurrentQuantity());
                return;
            }
        }

        OrderItem orderItem = new OrderItem(item, quantity);
        long orderItemId = orderItemRepository.addItem(id, orderItem);
        if(orderItemId > 0){
            orderItem.id = orderItemId;
        }
        items.add(orderItem);
    }

    public boolean removeItem(FoodItem item, int quantity){
        boolean result = false;
        OrderItem tempOrderItem = null;
        for(OrderItem orderItem: items){
            if(orderItem.foodItem.id == item.id){
                result = orderItem.decreaseQuantity(quantity);
                tempOrderItem = orderItem;
                break;
            }
        }

        if(tempOrderItem == null){
            return false;
        }

        if(result){
            items.remove(tempOrderItem);
            orderItemRepository.deleteById(tempOrderItem.id);
        } else {
            orderItemRepository.updateQuantity(tempOrderItem.id, tempOrderItem.getCurrentQuantity());
        }

        return true;
    }

    public void assignDeliveryPartner(DeliveryPartner deliveryPartner){
        this.deliveryPartner = deliveryPartner;
        orderRepository.assignDeliveryPartner(id, deliveryPartner == null ? null : deliveryPartner.getId());
    }

    public long getId() {
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

    public void setPayment(PaymentMode payment){
        this.payment = payment;

        long paymentId = paymentRepository.createPayment(payment.getName().equals("COD") ? "CASH_ON_DELIVERY" : payment.getName(), payment.getTransactionReferenceId());
        if(paymentId > 0){
            orderRepository.setPayment(id, paymentId);
        }
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
                "%-8d %-15s %-18s %-25s %-12.2f %-10.2f %-12.2f %-30s %-15s %-20s %-25s",
                id,
                (payment == null?"-":payment.getName()),
                (Objects.requireNonNull(payment).getTransactionReferenceId() == null?"-":payment.getTransactionReferenceId()),
                (deliveryPartner != null?
                deliveryPartner.getName() + " (" + deliveryPartner.getPhone() + ")":
                "Not Assigned Yet"),
                getTotalAmount(),
                getDiscount(),
                getFinalAmount(),
                status,
                (customer.getName() + " ( " + customer.getPhone() + " )"),
                (orderPlacedOn == null?"-":orderPlacedOn.format(formatter)),
                (discountDescription == null?"-":discountDescription)
        );
    }

    public String getDiscountDescription() {
        getDiscount();
        return discountDescription;
    }

    //Builder
    public static class Builder {

        private long id;
        private List<OrderItem> items;
        private String discountDescription;
        private PaymentMode payment;
        private DeliveryPartner deliveryPartner;
        private OrderStatus status;
        private LocalDateTime orderPlacedOn;
        private Customer customer;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setItems(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder setDiscountDescription(String discountDescription) {
            this.discountDescription = discountDescription;
            return this;
        }

        public Builder setPayment(PaymentMode payment) {
            this.payment = payment;
            return this;
        }

        public Builder setDeliveryPartner(DeliveryPartner deliveryPartner) {
            this.deliveryPartner = deliveryPartner;
            return this;
        }

        public Builder setStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder setOrderPlacedOn(LocalDateTime orderPlacedOn) {
            this.orderPlacedOn = orderPlacedOn;
            return this;
        }

        public Builder setCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Order build() {
            Order order = new Order(customer);

            // Override all fields
            order.id = this.id;

            order.items.clear();
            if (items != null) {
                order.items.addAll(items);
            }

            order.discountDescription = this.discountDescription;
            order.payment = this.payment;
            order.deliveryPartner = this.deliveryPartner;
            order.status = this.status;
            order.orderPlacedOn = this.orderPlacedOn;

            return order;
        }
    }
}
