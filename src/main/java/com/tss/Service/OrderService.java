package com.tss.Service;

import com.tss.Datatype.OrderStatus;
import com.tss.Repository.InMemoryUserRepository;
import com.tss.Utils.Display;
import com.tss.model.Notification;
import com.tss.model.Order;
import com.tss.model.User.DeliveryPartner;

import java.io.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import static com.tss.Utils.Print.success;

//Singleton
public class OrderService implements Serializable {

    private final Queue<Order> orderQueue;
    private final Queue<DeliveryPartner> deliveryPartnerQueue;

    private static final String filePath = "./Data/queues.ser";

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

    public void printOrderQueue(){
        Display.displayOrderHeading();
        orderQueue.forEach((i)->{
            success(i.toString());
        });
    }

    public void printDeliveryPartnerQueue(){
        Display.displayDeliveryPartnerHeading();
        deliveryPartnerQueue.forEach((i) -> {
            success(i.toString());
        });
    }

    static class Initiator{
        private static final OrderService instance = load();

        private static OrderService load(){
            File file = new File(filePath);

            if (!file.exists() || file.length() == 0) {
                return new OrderService();
            }
            try (ObjectInputStream in =
                         new ObjectInputStream(new FileInputStream(filePath))) {

                OrderService loadedInstance = (OrderService) in.readObject();
                return Objects.requireNonNullElseGet(loadedInstance, OrderService::new);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static OrderService getInstance(){
        return Initiator.instance;
    }

    public void saveState(){
        try(ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(filePath))){
            out.writeObject(this);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
