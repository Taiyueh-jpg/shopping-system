package com.shopping.dao.impl;

import com.shopping.dao.UserDao;
import com.shopping.model.User;
import com.shopping.util.DbConnection;
import java.sql.*;

public class UserDaoImpl implements UserDao {

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password, name) VALUES (?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public User queryUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("name")
                );
                user.setId(rs.getInt("id"));
                return user;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // 如果有查到資料，代表帳號已存在
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}