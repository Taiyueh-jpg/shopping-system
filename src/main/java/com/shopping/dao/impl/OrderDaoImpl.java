package com.shopping.dao.impl;

import com.shopping.dao.OrderDao;
import com.shopping.model.Order;
import com.shopping.model.OrderDetail;
import com.shopping.model.User;
import com.shopping.util.DbConnection;
import com.shopping.util.UserSession; // ★ 引入 Session 工具

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    // 1. 建立訂單 (修改這裡：自動抓取登入者)
    @Override
    public void createOrder(Order order) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false); // 開啟交易模式

            // ★★★ 關鍵修改：從 Session 抓名字 ★★★
            String finalCustomerName = order.getCustomerName(); // 預設使用傳進來的名字
            User currentUser = UserSession.getUser();           // 嘗試取得登入者
            
            if (currentUser != null) {
                finalCustomerName = currentUser.getName();      // 如果有登入，就用登入者的名字覆蓋
            }
            // ★★★★★★★★★★★★★★★★★★★★★★★★★

            // 寫入主檔
            String sqlOrder = "INSERT INTO orders (customer_name, total_amount) VALUES (?, ?)";
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, finalCustomerName); // 使用抓到的名字
            psOrder.setDouble(2, order.getTotalAmount());
            psOrder.executeUpdate();

            // 取得 ID
            rs = psOrder.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                throw new SQLException("建立訂單失敗，無法取得 ID");
            }

            // 寫入明細
            String sqlDetail = "INSERT INTO order_details (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            psDetail = conn.prepareStatement(sqlDetail);

            for (OrderDetail item : order.getDetails()) {
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getProductId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.setDouble(4, item.getUnitPrice());
                psDetail.addBatch();
            }
            psDetail.executeBatch();

            conn.commit(); // 提交

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            throw new RuntimeException("結帳失敗：" + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (rs != null) rs.close();
                if (psOrder != null) psOrder.close();
                if (psDetail != null) psDetail.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // 2. 查詢所有訂單
    @Override
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getString("customer_name"),
                    rs.getDouble("total_amount")
                );
                order.setId(rs.getInt("id"));
                order.setOrderDate(rs.getString("order_date"));
                list.add(order);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // 3. 依價格範圍查詢
    @Override
    public List<Order> getOrdersByPriceRange(double min, double max) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE total_amount BETWEEN ? AND ? ORDER BY total_amount DESC";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, min);
            ps.setDouble(2, max);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getString("customer_name"),
                        rs.getDouble("total_amount")
                    );
                    order.setId(rs.getInt("id"));
                    order.setOrderDate(rs.getString("order_date"));
                    list.add(order);
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}