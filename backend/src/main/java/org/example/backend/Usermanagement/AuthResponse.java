package org.example.backend.Usermanagement;

public class AuthResponse {
    private int code;
    private String message;
    private String token;

    // 私有构造函数，使用静态工厂方法创建实例
    private AuthResponse() {}

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public static AuthResponse success(String token) {
        AuthResponse response = new AuthResponse();
        response.code = 200;
        response.message = "登录成功";
        response.token = token;
        return response;
    }

    public static AuthResponse error(String message) {
        AuthResponse response = new AuthResponse();
        response.code = 401;
        response.message = message;
        response.token = null;
        return response;
    }
}