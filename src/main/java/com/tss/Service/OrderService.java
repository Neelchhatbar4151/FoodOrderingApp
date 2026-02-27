package com.tss.Service;

import com.tss.Datatype.OrderStatus;
import com.tss.model.Notification;
import com.tss.model.Order;
import com.tss.model.User.DeliveryPartner;

import java.util.LinkedList;
import java.util.Queue;

//Singleton
public class OrderService {

    private final Queue<Order> orderQueue;
    private final Queue<DeliveryPartner> deliveryPartnerQueue;

    private OrderService(){

        this.deliveryPartnerQueue = new LinkedList<>();
        this.orderQueue = new LinkedList<>();

    }

    public void checkQueue(){
        while(!deliveryPartnerQueue.isEmpty() && !orderQueue.isEmpty()){
            Order order = orderQueue.poll();
            if(order.getStatus() != OrderStatus.WAITING_FOR_DELIVERY_PARTNER){
                continue;
            }
            DeliveryPartner deliveryPartner = deliveryPartnerQueue.poll();
            order.assignDeliveryPartner(deliveryPartner);
            deliveryPartner.assignOrder(order);
            order.getCustomer().addNotification(new Notification("Delivery Partner: " + deliveryPartner.getName() + " ( " + deliveryPartner.getPhone() + " ) " + ", Assigned to your Order With Id: " + order.getId()));
            order.getDeliveryPartner().addNotification(new Notification("Order Assigned, Order Id: " + order.getId() + ", Customer: " + order.getCustomer().getName() + " ( " + order.getCustomer().getPhone() + " ) " + ", Address: " + (order.getCustomer().getAddress())));
            order.moveToNextState(true);
        }
    }

    public void addDeliveryPartner(DeliveryPartner deliveryPartner){
        deliveryPartnerQueue.add(deliveryPartner);

        checkQueue();
    }

    public void removeDeliveryPartner(DeliveryPartner deliveryPartner){
        deliveryPartnerQueue.remove(deliveryPartner);
    }

    public void addOrder(Order order){
        orderQueue.add(order);

        checkQueue();
    }

    static class Initiator{
        private static final OrderService instance = new OrderService();
    }

    public static OrderService getInstance(){
        return Initiator.instance;
    }

}
