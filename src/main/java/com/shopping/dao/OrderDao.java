package com.shopping.dao;
import java.util.List;

import com.shopping.model.Order;

public interface OrderDao {
    // 建立訂單 (包含主檔與明細)
    void createOrder(Order order);
    
    List<Order> getAllOrders();
    List<Order> getOrdersByPriceRange(double min, double max);
}