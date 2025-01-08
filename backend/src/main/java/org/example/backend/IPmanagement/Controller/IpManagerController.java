package org.example.backend.IPmanagement.Controller;

import org.example.backend.IPmanagement.Service.IpManagerService;
import org.example.backend.IPmanagement.model.IpRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ip-rules")
public class IpManagerController {

    @Autowired
    private IpManagerService ipManagerService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllIpRules() {
        List<IpRule> rules = ipManagerService.getAllIpRules();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", rules);
        response.put("message", "成功获取IP规则列表");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addIpRule(@RequestBody IpRule rule) {
        Map<String, Object> response = new HashMap<>();
        try {
            IpRule addedRule = ipManagerService.addIpRule(rule);
            response.put("code", 200);
            response.put("data", addedRule);
            response.put("message", "IP规则添加成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "添加IP规则失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Map<String, Object>> deleteIpRule(@PathVariable String ruleId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDeleted = ipManagerService.deleteIpRule(ruleId);
            if (isDeleted) {
                response.put("code", 200);
                response.put("message", "IP规则删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "未找到指定的IP规则");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除IP规则失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<Map<String, Object>> updateIpRule(
            @PathVariable String ruleId,
            @RequestBody IpRule rule) {
        Map<String, Object> response = new HashMap<>();
        try {
            IpRule updatedRule = ipManagerService.updateIpRule(ruleId, rule);
            if (updatedRule != null) {
                response.put("code", 200);
                response.put("data", updatedRule);
                response.put("message", "IP规则更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "未找到指定的IP规则");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新IP规则失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchAddIpRules(@RequestBody List<IpRule> rules) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<IpRule> addedRules = ipManagerService.batchAddIpRules(rules);
            response.put("code", 200);
            response.put("data", addedRules);
            response.put("message", "批量添加IP规则成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量添加IP规则失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteIpRules(@RequestBody List<String> ruleIds) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDeleted = ipManagerService.batchDeleteIpRules(ruleIds);
            if (isDeleted) {
                response.put("code", 200);
                response.put("message", "批量删除IP规则成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "部分或全部IP规则未找到");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量删除IP规则失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}