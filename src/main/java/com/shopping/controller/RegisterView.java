package com.shopping.controller;

import com.shopping.dao.UserDao;
import com.shopping.dao.impl.UserDaoImpl;
import com.shopping.model.User;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
    private JTextField userField, nameField;
    private JPasswordField passField;
    private UserDao userDao = new UserDaoImpl();

    public RegisterView() {
        setTitle("會員註冊");
        setBounds(100, 100, 350, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 關閉只關閉自己，不關閉整個程式
        setLayout(new GridLayout(4, 2, 10, 10)); // 簡單排版

        add(new JLabel("  設定帳號:"));
        userField = new JTextField();
        add(userField);

        add(new JLabel("  設定密碼:"));
        passField = new JPasswordField();
        add(passField);

        add(new JLabel("  您的暱稱:"));
        nameField = new JTextField();
        add(nameField);

        JButton btnRegister = new JButton("確認註冊");
        add(btnRegister);
        
        JButton btnCancel = new JButton("取消");
        add(btnCancel);

        // 註冊邏輯
        btnRegister.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String name = nameField.getText();

            if(username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "帳號密碼不能為空");
                return;
            }

            if(userDao.isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, "此帳號已被註冊！");
                return;
            }

            User u = new User(username, password, name);
            userDao.addUser(u);
            JOptionPane.showMessageDialog(this, "註冊成功！請登入");
            dispose(); // 關閉註冊視窗
        });

        btnCancel.addActionListener(e -> dispose());
    }
}