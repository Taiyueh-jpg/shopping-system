package com.shopping.controller;

import com.shopping.dao.UserDao;
import com.shopping.dao.impl.UserDaoImpl;
import com.shopping.model.User;
import com.shopping.util.UserSession; // ★ 記得要引入這個工具

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private UserDao userDao = new UserDaoImpl();

    public LoginView() {
        setTitle("購物系統 - 登入");
        setBounds(100, 100, 400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblTitle = new JLabel("歡迎來到億萬級購物系統");
        lblTitle.setFont(new Font("微軟正黑體", Font.BOLD, 18));
        lblTitle.setBounds(80, 20, 250, 30);
        add(lblTitle);

        JLabel lblUser = new JLabel("帳號:");
        lblUser.setBounds(50, 70, 60, 25);
        add(lblUser);

        userField = new JTextField();
        userField.setBounds(110, 70, 200, 25);
        userField.setText("admin"); // 方便測試預設值
        add(userField);

        JLabel lblPass = new JLabel("密碼:");
        lblPass.setBounds(50, 110, 60, 25);
        add(lblPass);

        passField = new JPasswordField();
        passField.setBounds(110, 110, 200, 25);
        passField.setText("1234"); // 方便測試預設值
        add(passField);

        JButton btnLogin = new JButton("登入");
        btnLogin.setBounds(110, 160, 90, 30);
        add(btnLogin);

        JButton btnReg = new JButton("註冊");
        btnReg.setBounds(220, 160, 90, 30);
        add(btnReg);

        // --- 登入按鈕邏輯 ---
        btnLogin.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            User user = userDao.queryUser(username, password);

            if (user != null) {
                // ★★★ 關鍵修改：將登入者存入 Session ★★★
                UserSession.setUser(user);
                
                JOptionPane.showMessageDialog(this, "歡迎回來，" + user.getName());
                
                // 開啟主系統
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                
                // 關閉登入視窗
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "登入失敗，帳號或密碼錯誤", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- 註冊按鈕邏輯 ---
        btnReg.addActionListener(e -> {
            new RegisterView().setVisible(true);
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginView frame = new LoginView();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}