package com.tss.Service;

import com.tss.DB.DBConnection;
import com.tss.Datatype.OrderStatus;
import com.tss.Repository.UserRepository;
import com.tss.Utils.Display;
import com.tss.Utils.GlobalVariables;
import com.tss.model.Notification;
import com.tss.model.Order;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.Queue;

import static com.tss.Utils.Print.success;

public class OrderService  {

    private final Queue<Order> orderQueue;
    private final Queue<DeliveryPartner> deliveryPartnerQueue;
    private final UserRepository userRepository;

    private OrderService(){
        this.deliveryPartnerQueue = new LinkedList<>();
        this.orderQueue = new LinkedList<>();
        this.userRepository = GlobalVariables.getInstance().userRepository;
    }

    private void assignFromQueues() {
        while(!deliveryPartnerQueue.isEmpty() && !orderQueue.isEmpty()){
            Order order = orderQueue.poll();
            if(order == null || order.getStatus() != OrderStatus.WAITING_FOR_DELIVERY_PARTNER){
                continue;
            }
            DeliveryPartner deliveryPartner = deliveryPartnerQueue.poll();
            if (deliveryPartner == null || !deliveryPartner.getIsApproved()) {
                continue;
            }
            order.assignDeliveryPartner(deliveryPartner);
            deliveryPartner.assignOrder(order);
            order.getCustomer().addNotification(new Notification("Delivery Partner: " + deliveryPartner.getName() + " ( " + deliveryPartner.getPhone() + " ) " + ", Assigned to your Order With Id: " + order.getId()));
            order.getDeliveryPartner().addNotification(new Notification("Order Assigned, Order Id: " + order.getId() + ", Customer: " + order.getCustomer().getName() + " ( " + order.getCustomer().getPhone() + " ) " + ", Address: " + (order.getCustomer().getAddress())));
            order.moveToNextState(true);
        }
    }

    private void assignPendingOrdersFromDbByPriority() {
        try (Connection conn = DBConnection.getConnection()) {
            while (true) {
                String orderQuery = """
                        SELECT order_id, customer_id
                        FROM orders
                        WHERE order_status = 'WAITING_FOR_DELIVERY_PARTNER'::order_status
                          AND delivery_partner_id IS NULL
                        ORDER BY order_placed_on NULLS LAST, order_id
                        LIMIT 1
                        """;

                PreparedStatement orderPs = conn.prepareStatement(orderQuery);
                ResultSet orderRs = orderPs.executeQuery();
                if (!orderRs.next()) {
                    return;
                }

                long orderId = orderRs.getLong("order_id");
                long customerId = orderRs.getLong("customer_id");

                String partnerQuery = """
                        SELECT delivery_partner_id
                        FROM delivery_partner
                        WHERE is_available = true
                          AND is_approved = true
                        ORDER BY priority ASC, delivery_partner_id ASC
                        LIMIT 1
                        """;

                PreparedStatement partnerPs = conn.prepareStatement(partnerQuery);
                ResultSet partnerRs = partnerPs.executeQuery();
                if (!partnerRs.next()) {
                    return;
                }

                long partnerId = partnerRs.getLong("delivery_partner_id");
                User partnerUser = userRepository.getUserById((int) partnerId);
                User customerUser = userRepository.getUserById((int) customerId);
                if (!(partnerUser instanceof DeliveryPartner deliveryPartner) || customerUser == null) {
                    return;
                }

                String assignSql = """
                        UPDATE orders
                        SET delivery_partner_id = ?,
                            order_status = 'OUT_FOR_DELIVERY'::order_status
                        WHERE order_id = ?
                          AND order_status = 'WAITING_FOR_DELIVERY_PARTNER'::order_status
                          AND delivery_partner_id IS NULL
                        """;

                PreparedStatement assignPs = conn.prepareStatement(assignSql);
                assignPs.setLong(1, partnerId);
                assignPs.setLong(2, orderId);
                int assigned = assignPs.executeUpdate();
                if (assigned == 0) {
                    continue;
                }

                PreparedStatement availabilityPs = conn.prepareStatement("UPDATE delivery_partner SET is_available = false WHERE delivery_partner_id = ?");
                availabilityPs.setLong(1, partnerId);
                availabilityPs.executeUpdate();

                PreparedStatement mappingPs = conn.prepareStatement("INSERT INTO delivery_assignment(order_id, delivery_partner_id) VALUES (?, ?) ON CONFLICT DO NOTHING");
                mappingPs.setLong(1, orderId);
                mappingPs.setLong(2, partnerId);
                mappingPs.executeUpdate();

                deliveryPartner.addNotification(new Notification("Order Assigned, Order Id: " + orderId));
                customerUser.addNotification(new Notification("Delivery Partner: " + deliveryPartner.getName() + " ( " + deliveryPartner.getPhone() + " ) , Assigned to your Order With Id: " + orderId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkQueue(){
        assignFromQueues();
        assignPendingOrdersFromDbByPriority();
    }

    public void addDeliveryPartner(DeliveryPartner deliveryPartner){
        if (deliveryPartner == null || !deliveryPartner.getIsApproved()) {
            return;
        }
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
        private static final OrderService instance = new OrderService();
    }

    public static OrderService getInstance(){
        return Initiator.instance;
    }
}
