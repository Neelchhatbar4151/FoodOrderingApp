package com.tss.Service;

import com.tss.DB.DBConnection;
import com.tss.model.Notification;
import com.tss.model.Order;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.tss.Utils.Print.success;

public class OrderService  {

    private OrderService(){
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

                User partnerUser = com.tss.Utils.GlobalVariables.getInstance().userRepository.getUserById((int) partnerId);
                User customerUser = com.tss.Utils.GlobalVariables.getInstance().userRepository.getUserById((int) customerId);
                if (partnerUser instanceof DeliveryPartner deliveryPartner && customerUser != null) {
                    deliveryPartner.setAssignedOrderFromDb(com.tss.Utils.GlobalVariables.getInstance().orderRepository.getActiveDeliveryByPartnerId(partnerId));
                    deliveryPartner.addNotification(new Notification("Order Assigned, Order Id: " + orderId));
                    customerUser.addNotification(new Notification("Delivery Partner: " + deliveryPartner.getName() + " ( " + deliveryPartner.getPhone() + " ) , Assigned to your Order With Id: " + orderId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDeliveryPartner(DeliveryPartner deliveryPartner){
        if (deliveryPartner == null || !deliveryPartner.getIsApproved()) {
            return;
        }
        assignPendingOrdersFromDbByPriority();
    }

    public void removeDeliveryPartner(DeliveryPartner deliveryPartner){
    }

    public void addOrder(Order order){
        assignPendingOrdersFromDbByPriority();
    }

    public void printOrderQueue(){
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    SELECT o.order_id
                    FROM orders o
                    WHERE o.order_status = 'WAITING_FOR_DELIVERY_PARTNER'::order_status
                      AND o.delivery_partner_id IS NULL
                    ORDER BY o.order_placed_on NULLS LAST, o.order_id
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                success("Order Id: " + rs.getLong("order_id") + " | WAITING_FOR_DELIVERY_PARTNER");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printDeliveryPartnerQueue(){
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    SELECT delivery_partner_id, priority
                    FROM delivery_partner
                    WHERE is_available = true AND is_approved = true
                    ORDER BY priority ASC, delivery_partner_id ASC
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                success("Delivery Partner Id: " + rs.getLong("delivery_partner_id") + " | priority=" + rs.getLong("priority") + " | AVAILABLE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Initiator{
        private static final OrderService instance = new OrderService();
    }

    public static OrderService getInstance(){
        return Initiator.instance;
    }
}
