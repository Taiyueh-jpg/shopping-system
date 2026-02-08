package com.shopping.model;

public class Product {
    private Integer id;
    private String name;
    private Double price;
    private Integer stock;
    private String description;

    // 建構子
    public Product(String name, Double price, Integer stock, String description) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    // Getters
    public Integer getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getDescription() { return description; }

    // Setters (這次新增的關鍵！)
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setDescription(String description) { this.description = description; }
}