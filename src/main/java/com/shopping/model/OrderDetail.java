package com.shopping.model;

public class OrderDetail {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Double unitPrice;

    
    public OrderDetail() {
    }

    
    public OrderDetail(Integer productId, Integer quantity, Double unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; } // 這行救了 ShoppingView

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
}