package com.tss.Service;

import com.tss.model.Order;
import com.tss.model.User.DeliveryPartner;

import java.util.LinkedList;
import java.util.Queue;

public class OrderService {
    Queue<Order> orderQueue;
    Queue<DeliveryPartner> deliveryPartnerQueue;

    public OrderService(){
        this.deliveryPartnerQueue = new LinkedList<>();
        this.orderQueue = new LinkedList<>();
    }


}
