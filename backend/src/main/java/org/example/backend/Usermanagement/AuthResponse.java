package org.example.backend.Usermanagement;

public class AuthResponse {
    private String message;
    private String token;

    // 私有构造函数，使用静态工厂方法创建实例
    private AuthResponse() {}

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public static AuthResponse success(String token) {
        AuthResponse response = new AuthResponse();
        response.message = "登录成功！";
        response.token = token;
        return response;
    }

    public static AuthResponse error(String message) {
        AuthResponse response = new AuthResponse();
        response.message = message;
        return response;
    }
}