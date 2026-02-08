package com.shopping.dao;
import com.shopping.model.User;

public interface UserDao {
    // 註冊 (新增會員)
    void addUser(User user);
    
    // 登入驗證 (回傳 User 代表成功，回傳 null 代表失敗)
    User queryUser(String username, String password);
    
    // 檢查帳號是否重複
    boolean isUsernameTaken(String username);
}