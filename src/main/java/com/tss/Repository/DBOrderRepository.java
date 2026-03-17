package com.tss.Repository;

import com.tss.Datatype.OrderStatus;
import com.tss.DB.DBConnection;
import com.tss.model.Order;
import com.tss.model.OrderItem;
import com.tss.model.User.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBOrderRepository implements OrderRepository {

    private final OrderItemRepository orderItemRepository;

    private DBOrderRepository() {
        this.orderItemRepository = DBOrderItemRepository.getInstance();
    }

    @Override
    public long createOrder(Order order) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    INSERT INTO orders(payment_id, delivery_partner_id, order_status, order_placed_on, customer_id, discount_amount)
                    VALUES (?, ?, ?::order_status, ?, ?, ?)
                    RETURNING order_id
                    """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setNull(1, java.sql.Types.BIGINT);

            if (order.getDeliveryPartner() == null) {
                ps.setNull(2, java.sql.Types.BIGINT);
            } else {
                ps.setLong(2, order.getDeliveryPartner().getId());
            }

            ps.setString(3, order.getStatus().name());
            ps.setTimestamp(4, order.getOrderPlacedOn() == null ? null : Timestamp.valueOf(order.getOrderPlacedOn()));
            ps.setLong(5, order.getCustomer().getId());
            ps.setDouble(6, order.getDiscount());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long orderId = rs.getLong("order_id");
                orderItemRepository.addItems(orderId, order.getItems());
                return orderId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    @Override
    public Order createCart(Customer customer) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    INSERT INTO orders(payment_id, delivery_partner_id, order_status, order_placed_on, customer_id, discount_amount)
                    VALUES (?, ?, ?::order_status, ?, ?, ?)
                    RETURNING *
                    """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setNull(1, java.sql.Types.BIGINT);
            ps.setNull(2, java.sql.Types.BIGINT);
            ps.setString(3, OrderStatus.CREATED.name());
            ps.setTimestamp(4, null);
            ps.setLong(5, customer.getId());
            ps.setDouble(6, 0);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildOrder(rs, customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updateStatus(long orderId, OrderStatus status) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE orders SET order_status = ?::order_status WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, status.name());
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    @Override
    public boolean updatePlacedOn(long orderId, LocalDateTime orderPlacedOn) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE orders SET order_placed_on = ? WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setTimestamp(1, orderPlacedOn == null ? null : Timestamp.valueOf(orderPlacedOn));
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean assignDeliveryPartner(long orderId, Long deliveryPartnerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE orders SET delivery_partner_id = ? WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            if (deliveryPartnerId == null) {
                ps.setNull(1, java.sql.Types.BIGINT);
            } else {
                ps.setLong(1, deliveryPartnerId);
            }
            ps.setLong(2, orderId);
            boolean updated = ps.executeUpdate() > 0;
            if (!updated || deliveryPartnerId == null) {
                return updated;
            }

            PreparedStatement mappingPs = conn.prepareStatement("INSERT INTO delivery_assignment(order_id, delivery_partner_id) VALUES (?, ?) ON CONFLICT DO NOTHING");
            mappingPs.setLong(1, orderId);
            mappingPs.setLong(2, deliveryPartnerId);
            mappingPs.executeUpdate();

            PreparedStatement partnerPs = conn.prepareStatement("UPDATE delivery_partner SET is_available = false WHERE delivery_partner_id = ?");
            partnerPs.setLong(1, deliveryPartnerId);
            partnerPs.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean setPayment(long orderId, Long paymentId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE orders SET payment_id = ? WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            if (paymentId == null) {
                ps.setNull(1, java.sql.Types.BIGINT);
            } else {
                ps.setLong(1, paymentId);
            }
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Order getById(long orderId, Customer customer) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM orders WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, orderId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }

            return buildOrder(rs, customer);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public Order getActiveCartByCustomerId(long customerId, Customer customer) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    SELECT *
                    FROM orders
                    WHERE customer_id = ? AND order_status = 'CREATED'::order_status
                    ORDER BY order_id DESC
                    LIMIT 1
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, customerId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }

            return buildOrder(rs, customer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Order> getByCustomerId(long customerId, Customer customer) {
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_id DESC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, customerId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(buildOrder(rs, customer));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    private Order buildOrder(ResultSet rs, Customer customer) throws Exception {
        long orderId = rs.getLong("order_id");
        List<OrderItem> items = orderItemRepository.getByOrderId(orderId);

        return new Order.Builder()
                .setId(orderId)
                .setCustomer(customer)
                .setItems(items)
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setOrderPlacedOn(rs.getTimestamp("order_placed_on") == null ? null : rs.getTimestamp("order_placed_on").toLocalDateTime())
                .build();
    }

    public static class Initiator {
        private static final DBOrderRepository instance = new DBOrderRepository();
    }

    public static DBOrderRepository getInstance() {
        return Initiator.instance;
    }
}
