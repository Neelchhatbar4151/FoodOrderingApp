package com.tss.Repository;

import com.tss.model.OrderItem;

import java.util.List;

public interface OrderItemRepository {
    void addItem(long orderId, OrderItem item);
    void addItems(long orderId, List<OrderItem> items);
    List<OrderItem> getByOrderId(long orderId);
    boolean deleteByOrderId(long orderId);
}
