package org.example.backend.Usermanagement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private UserManagementRepository userManagementRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String userPassword = loginRequest.getUserPassword();

        ResponseEntity<AuthResponse> serviceResponse = userManagementService.validateUser(userId, userPassword);
        AuthResponse authResponse = serviceResponse.getBody();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", serviceResponse.getStatusCodeValue());
        
        if (authResponse != null && authResponse.getToken() != null) {
            Map<String, String> data = new HashMap<>();
            data.put("token", authResponse.getToken());
            response.put("data", data);
            response.put("message", authResponse.getMessage());
        } else {
            response.put("message", authResponse != null ? authResponse.getMessage() : "登录失败");
        }
        
        return ResponseEntity.status(serviceResponse.getStatusCode()).body(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        try {
            Claims claims = jwtUtil.extractClaims(token);
            String userId = claims.getSubject();
            jwtUtil.validateToken(token, userId);
            return ResponseEntity.ok("用户已登出！");
        } catch (SignatureException | IllegalArgumentException e) {
            // 捕获JWT验证异常，例如签名错误或令牌格式错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("无效的令牌！");
        } catch (Exception e) {
            // 捕获其他可能的异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器错误！");
        }
    }

    @GetMapping("/info")
    public Object getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        try {
            Claims claims = jwtUtil.extractClaims(token); // 假设JwtUtil有一个extractClaims方法来提取令牌中的信息
            String userId = claims.getSubject();

            User user = userManagementRepository.findByUserId(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(user);
        } catch (SignatureException | IllegalArgumentException e) {
            // 捕获JWT验证异常
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未授权访问！");
        } catch (Exception e) {
            // 捕获其他可能的异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String extractToken(String authorizationHeader) {
        String[] parts = authorizationHeader.split(" ");
        if (parts.length != 2 || !"Bearer".equalsIgnoreCase(parts[0])) {
            throw new IllegalArgumentException("无效的Authorization头格式。请使用'Bearer <token>'格式。");
        }
        return parts[1];
    }
}

class LoginRequest {
    private String userId;
    private String userPassword;

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
