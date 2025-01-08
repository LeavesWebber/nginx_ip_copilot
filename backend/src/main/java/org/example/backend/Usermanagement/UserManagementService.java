package org.example.backend.Usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {

    @Autowired
    private UserManagementRepository userManagementRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<AuthResponse> validateUser(String userId, String userPassword) {
        try {
            User user = userManagementRepository.findByUserId(userId);
            if (user != null && user.getUserPassword().equals(userPassword)) {
                String jwt = jwtUtil.generateToken(userId);
                return ResponseEntity.ok(AuthResponse.success(jwt));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.error("用户名或密码错误"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AuthResponse.error("服务器内部错误"));
        }
    }
}
