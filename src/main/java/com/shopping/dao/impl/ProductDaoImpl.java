package com.shopping.dao.impl;

import com.shopping.dao.ProductDao;
import com.shopping.model.Product;
import com.shopping.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    @Override
    public void addProduct(Product p) {
        String sql = "INSERT INTO products (name, price, stock, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = new Product(
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("description")
                );
                p.setId(rs.getInt("id"));
                list.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ★★★ 修正這裡：原本漏掉了 description 的更新 ★★★
    @Override
    public void updateProduct(Product p) {
        // SQL 加入了 description=?
        String sql = "UPDATE products SET name=?, price=?, stock=?, description=? WHERE id=?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getDescription()); // 設定描述
            ps.setInt(5, p.getId());             // 設定 ID
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}