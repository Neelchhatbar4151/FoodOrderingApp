package com.tss.Repository;

import com.tss.Datatype.OrderStatus;
import com.tss.model.Order;
import com.tss.model.User.Customer;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository {
    long createOrder(Order order);
    Order createCart(Customer customer);
    boolean updateStatus(long orderId, OrderStatus status);
    boolean updatePlacedOn(long orderId, LocalDateTime orderPlacedOn);
    boolean assignDeliveryPartner(long orderId, Long deliveryPartnerId);
    boolean setPayment(long orderId, Long paymentId);
    Order getById(long orderId, Customer customer);
    Order getActiveCartByCustomerId(long customerId, Customer customer);
    List<Order> getByCustomerId(long customerId, Customer customer);
    Order getActiveDeliveryByPartnerId(long deliveryPartnerId);
}
