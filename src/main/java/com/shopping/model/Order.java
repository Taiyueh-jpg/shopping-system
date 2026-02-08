package com.shopping.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id;
    private String customerName;
    private Double totalAmount;
    // ▼ 新增這個欄位來存日期
    private String orderDate; 
    
    private List<OrderDetail> details = new ArrayList<>();

    public Order(String customerName, Double totalAmount) {
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    public void addDetail(OrderDetail detail) {
        this.details.add(detail);
    }

    public List<OrderDetail> getDetails() { return details; }
    
    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public Double getTotalAmount() { return totalAmount; }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    // ▼ 新增這兩個方法，讓 DAO 可以存取日期
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
}