package com.tss.Repository;

import com.tss.model.OrderItem;

import java.util.List;

public interface OrderItemRepository {
    long addItem(long orderId, OrderItem item);
    void addItems(long orderId, List<OrderItem> items);
    boolean updateQuantity(long orderItemId, int quantity);
    boolean deleteById(long orderItemId);
    List<OrderItem> getByOrderId(long orderId);
    boolean deleteByOrderId(long orderId);
}
