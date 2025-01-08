package org.example.backend.IPzonemanagement;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/geo-rules")
public class IPzoneManagementController {

    @Autowired
    private IPzoneManagementService ipzoneManagementService;

    @GetMapping
    public ResponseEntity<List<GeoRule>> getAllGeoRules() {
        List<GeoRule> geoRules = ipzoneManagementService.getAllGeoRules();
        return ResponseEntity.ok(geoRules);
    }

    @PostMapping
    public ResponseEntity<GeoRule> addGeoRule(@RequestBody GeoRule geoRule) throws IOException {
        GeoRule savedGeoRule = ipzoneManagementService.addGeoRule(geoRule);
        return ResponseEntity.ok(savedGeoRule);
    }

    @DeleteMapping("/{rule_id}")
    public ResponseEntity<Map<String, Object>> deleteGeoRule(@PathVariable String rule_id) throws IOException {
        Map<String, Object> response = new HashMap<>();
        try {
            ipzoneManagementService.deleteGeoRule(rule_id);
            // 获取更新后的规则列表
            List<GeoRule> updatedRules = ipzoneManagementService.getAllGeoRules();
            response.put("code", 200);
            response.put("message", "规则删除成功");
            response.put("data", updatedRules);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除规则失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}