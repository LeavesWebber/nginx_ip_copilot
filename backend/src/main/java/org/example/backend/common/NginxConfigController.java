package org.example.backend.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/nginx")
public class NginxConfigController {

    private static final Logger logger = LoggerFactory.getLogger(NginxConfigController.class);

    @Autowired
    private NginxConfigService nginxConfigService;

    @PostMapping("/config/path/validate")
    public ResponseEntity<?> validateConfigPath(@RequestBody Map<String, String> request) {
        String path = request.get("path");
        Map<String, Object> validation = nginxConfigService.validateConfigPath(path);
        boolean isValid = (boolean)validation.get("exists") 
            && (boolean)validation.get("isFile")
            && (boolean)validation.get("canRead")
            && (boolean)validation.get("canWrite");
        return ResponseEntity.ok(Map.of(
            "exists", isValid,
            "details", validation
        ));
    }

    @GetMapping("/config")
    public ResponseEntity<?> getConfig() {
        String config = nginxConfigService.readConfig();
        return ResponseEntity.ok(Map.of("config", config));
    }

    @PostMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        try {
            // 更新配置文件
            nginxConfigService.updateConfig(content);
            
            // 尝试重载Nginx，但不影响配置文件更新的结果
            try {
                nginxConfigService.reloadNginx();
            } catch (Exception e) {
                logger.warn("Nginx reload failed: " + e.getMessage());
                // 返回配置更新成功但重载失败的信息
                return ResponseEntity.ok(Map.of(
                    "message", "配置已更新，但Nginx重载失败：请检查 nginx 是否正常运行",
                    "config", content,
                    "reloadSuccess", false
                ));
            }
            
            // 配置更新和重载都成功
            return ResponseEntity.ok(Map.of(
                "message", "配置已更新并已重载Nginx",
                "config", content,
                "reloadSuccess", true
            ));
        } catch (Exception e) {
            // 配置更新失败
            return ResponseEntity.status(500).body(Map.of(
                "message", "配置更新失败：" + e.getMessage()
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getNginxStatus() {
        Map<String, Object> status = nginxConfigService.getNginxStatus();
        return ResponseEntity.ok(status);
    }

    @PostMapping("/reload")
    public ResponseEntity<?> reloadNginx() {
        boolean success = nginxConfigService.reloadNginx();
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        Map<String, Object> settings = nginxConfigService.getSettings();
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, Object> settings) {
        nginxConfigService.updateSettings(settings);
        return ResponseEntity.ok(settings);
    }
} 