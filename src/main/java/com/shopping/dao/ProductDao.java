package com.shopping.dao;

import java.util.List;
import com.shopping.model.Product;

public interface ProductDao {
    // 新增
    void addProduct(Product p);
    
    // 查詢全部
    List<Product> getAllProducts();
    
    // 修改 (AdminView 會用到)
    void updateProduct(Product p);
    
    // 刪除 (AdminView 會用到)
    void deleteProduct(int id);
}