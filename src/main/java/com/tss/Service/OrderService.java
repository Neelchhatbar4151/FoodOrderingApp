package com.tss.Service;

import com.tss.Datatype.OrderStatus;
import com.tss.model.Order;
import com.tss.model.User.DeliveryPartner;

import java.util.LinkedList;
import java.util.Queue;

//Singleton
public class OrderService {
    Queue<Order> orderQueue;
    Queue<DeliveryPartner> deliveryPartnerQueue;

    private OrderService(){
        this.deliveryPartnerQueue = new LinkedList<>();
        this.orderQueue = new LinkedList<>();
    }

    public void checkQueue(){
        while(!deliveryPartnerQueue.isEmpty()){
            Order order = orderQueue.poll();
            if(order.getStatus() != OrderStatus.WAITING_FOR_DELIVERY_PARTNER){
                continue;
            }
            DeliveryPartner deliveryPartner = deliveryPartnerQueue.poll();
            order.assignDeliveryPartner(deliveryPartner);
            deliveryPartner.assignOrder(order);
            order.moveToNextState(true);
        }
    }

    public void addDeliveryPartner(DeliveryPartner deliveryPartner){
        deliveryPartnerQueue.add(deliveryPartner);

        checkQueue();
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
