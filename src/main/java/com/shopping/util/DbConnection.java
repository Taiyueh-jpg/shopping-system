package com.shopping.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/shopping_db?serverTimezone=UTC";
    private static final String USER = "root"; // 請更改為您的帳號
    private static final String PASSWORD = "1234"; // 請更改為您的密碼

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("資料庫連線失敗: " + e.getMessage());
        }
    }
}