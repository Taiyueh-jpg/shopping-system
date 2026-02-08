package com.shopping.controller;

import com.shopping.dao.ProductDao;
import com.shopping.dao.impl.ProductDaoImpl;
import com.shopping.model.Product;
import com.shopping.util.UserSession; // 引入 Session

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminView extends JFrame {
    
    // 介面元件
    private JTextField idField, nameField, priceField, stockField, descField;
    private JTable table;
    private DefaultTableModel model;
    
    private ProductDao productDao = new ProductDaoImpl();

    public AdminView() {
        setTitle("後台 - 商品管理系統");
        setBounds(100, 100, 800, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // === 新增：登出按鈕 (放在右上角) ===
        JButton btnLogout = new JButton("登出系統");
        btnLogout.setBounds(680, 20, 90, 25);
        btnLogout.setForeground(Color.RED);
        add(btnLogout);

        btnLogout.addActionListener(e -> {
            // 1. 清除使用者紀錄
            UserSession.setUser(null);
            // 2. 關閉目前視窗
            dispose();
            // 3. 回到登入畫面
            new LoginView().setVisible(true);
        });

        // === 第一排：基本資訊 ===
        
        // ID
        JLabel lblId = new JLabel("編號:");
        lblId.setBounds(20, 20, 40, 25);
        add(lblId);

        idField = new JTextField();
        idField.setBounds(60, 20, 60, 25);
        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);
        add(idField);

        // 名稱
        JLabel lblName = new JLabel("名稱:");
        lblName.setBounds(130, 20, 40, 25);
        add(lblName);

        nameField = new JTextField();
        nameField.setBounds(170, 20, 150, 25);
        add(nameField);

        // 價格
        JLabel lblPrice = new JLabel("價格:");
        lblPrice.setBounds(330, 20, 40, 25);
        add(lblPrice);

        priceField = new JTextField();
        priceField.setBounds(370, 20, 80, 25);
        add(priceField);

        // 庫存
        JLabel lblStock = new JLabel("庫存:");
        lblStock.setBounds(460, 20, 40, 25);
        add(lblStock);

        stockField = new JTextField();
        stockField.setBounds(500, 20, 80, 25);
        add(stockField);

        // === 第二排：描述 ===
        
        JLabel lblDesc = new JLabel("描述:");
        lblDesc.setBounds(20, 60, 40, 25);
        add(lblDesc);

        descField = new JTextField();
        descField.setBounds(60, 60, 520, 25);
        add(descField);

        // === 第三排：按鈕區 ===
        
        JButton btnAdd = new JButton("新增");
        btnAdd.setBounds(20, 100, 80, 30);
        add(btnAdd);

        JButton btnUpdate = new JButton("修改");
        btnUpdate.setBounds(110, 100, 80, 30);
        add(btnUpdate);

        JButton btnDelete = new JButton("刪除");
        btnDelete.setBounds(200, 100, 80, 30);
        add(btnDelete);
        
        JButton btnClear = new JButton("清空");
        btnClear.setBounds(290, 100, 80, 30);
        add(btnClear);

        // === 第四排：表格 ===
        
        String[] columnNames = {"ID", "名稱", "價格", "庫存", "描述"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 150, 740, 430);
        add(scrollPane);

        // === 事件邏輯 ===

        loadData();

        // 表格點擊事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                
                // 將資料填回上方欄位
                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                priceField.setText(model.getValueAt(row, 2).toString());
                stockField.setText(model.getValueAt(row, 3).toString());
                
                Object descObj = model.getValueAt(row, 4);
                descField.setText(descObj != null ? descObj.toString() : "");
            }
        });

        // 新增
        btnAdd.addActionListener(e -> {
            try {
                String name = nameField.getText();
                Double price = Double.parseDouble(priceField.getText());
                Integer stock = Integer.parseInt(stockField.getText());
                String desc = descField.getText();

                Product p = new Product(name, price, stock, desc);
                productDao.addProduct(p);
                
                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "新增成功！");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "格式錯誤：" + ex.getMessage());
            }
        });

        // 修改
        btnUpdate.addActionListener(e -> {
            String idStr = idField.getText();
            if(idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "請先選擇商品！");
                return;
            }
            try {
                int id = Integer.parseInt(idStr);
                String name = nameField.getText();
                Double price = Double.parseDouble(priceField.getText());
                Integer stock = Integer.parseInt(stockField.getText());
                String desc = descField.getText();

                Product p = new Product(name, price, stock, desc);
                p.setId(id);

                productDao.updateProduct(p);
                
                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "修改成功！");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "修改失敗：" + ex.getMessage());
            }
        });

        // 刪除
        btnDelete.addActionListener(e -> {
            String idStr = idField.getText();
            if(idStr.isEmpty()) return;
            
            if (JOptionPane.showConfirmDialog(this, "確定刪除？", "確認", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                productDao.deleteProduct(Integer.parseInt(idStr));
                loadData();
                clearFields();
            }
        });
        
        btnClear.addActionListener(e -> clearFields());
    }

    private void loadData() {
        model.setRowCount(0);
        List<Product> list = productDao.getAllProducts();
        for (Product p : list) {
            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getStock(),
                p.getDescription()
            });
        }
    }
    
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        descField.setText("");
    }
}