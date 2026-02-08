// ▼ 重點：這裡的 package 變成了 controller
package com.shopping.controller;

// Import 記得要引入 Model 和 DAO，因為它們住在不同的包
import com.shopping.dao.OrderDao;
import com.shopping.dao.impl.OrderDaoImpl;
import com.shopping.model.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderManagementView extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private OrderDao orderDao = new OrderDaoImpl();

    public OrderManagementView() {
        setTitle("後台 - 訂單管理系統");
        setBounds(100, 100, 700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // === 1. 上方搜尋面板 ===
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(230, 230, 250)); 
        topPanel.add(new JLabel("價格範圍搜尋: $"));
        
        minPriceField = new JTextField(6);
        minPriceField.setText("0"); 
        topPanel.add(minPriceField);
        
        topPanel.add(new JLabel(" ~ $"));
        
        maxPriceField = new JTextField(6);
        maxPriceField.setText("99999"); 
        topPanel.add(maxPriceField);
        
        JButton btnSearch = new JButton("查詢");
        topPanel.add(btnSearch);
        
        JButton btnRefresh = new JButton("顯示全部");
        topPanel.add(btnRefresh);
        
        add(topPanel, BorderLayout.NORTH);

        // === 2. 中間表格資料 ===
        String[] columnNames = {"訂單編號", "客戶名稱", "訂單金額", "下單時間"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // === 3. 底部狀態 ===
        JPanel bottomPanel = new JPanel();
        JLabel lblStatus = new JLabel("準備就緒");
        bottomPanel.add(lblStatus);
        add(bottomPanel, BorderLayout.SOUTH);

        // === 4. 事件邏輯 ===
        loadData(orderDao.getAllOrders());

        btnSearch.addActionListener(e -> {
            try {
                double min = Double.parseDouble(minPriceField.getText());
                double max = Double.parseDouble(maxPriceField.getText());
                
                List<Order> results = orderDao.getOrdersByPriceRange(min, max);
                loadData(results);
                lblStatus.setText("搜尋完成，共找到 " + results.size() + " 筆訂單");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "請輸入正確的數字金額！");
            }
        });

        btnRefresh.addActionListener(e -> {
            loadData(orderDao.getAllOrders());
            minPriceField.setText("0");
            maxPriceField.setText("99999");
            lblStatus.setText("顯示所有資料");
        });
    }

    private void loadData(List<Order> orders) {
        model.setRowCount(0); 
        for (Order o : orders) {
            model.addRow(new Object[]{
                o.getId(),
                o.getCustomerName(),
                o.getTotalAmount(),
                o.getOrderDate()
            });
        }
    }
}