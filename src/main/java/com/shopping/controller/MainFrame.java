package com.shopping.controller;

import javax.swing.*;
import java.awt.*;
import com.shopping.util.UserSession; // 引入 Session

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("億萬級購物系統 - 主選單");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 50)); // 置中對齊

        JButton btnShop = new JButton("前台：客戶購物");
        btnShop.setPreferredSize(new Dimension(150, 50));
        
        JButton btnAdmin = new JButton("後台：管理員系統");
        btnAdmin.setPreferredSize(new Dimension(150, 50));

        // === 新增：登出按鈕 ===
        JButton btnLogout = new JButton("登出 / 切換帳號");
        btnLogout.setPreferredSize(new Dimension(150, 50));
        btnLogout.setBackground(Color.PINK);

        // 前台按鈕事件
        btnShop.addActionListener(e -> {
            new ShoppingView().setVisible(true); 
        });

        // 後台按鈕事件
        btnAdmin.addActionListener(e -> {
            String[] options = {"商品管理 (CRUD)", "訂單管理 (查詢)", "取消"};
            int choice = JOptionPane.showOptionDialog(
                this, 
                "請問您要進入哪個後台系統？", 
                "後台選擇", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                options, 
                options[0]
            );

            if (choice == 0) {
                new AdminView().setVisible(true); 
            } else if (choice == 1) {
                new OrderManagementView().setVisible(true); 
            }
        });

        // 登出按鈕事件
        btnLogout.addActionListener(e -> {
            // 清空 Session 並回到登入畫面
            UserSession.setUser(null);
            dispose();
            new LoginView().setVisible(true);
        });

        add(btnShop);
        add(btnAdmin);
        add(btnLogout);
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}