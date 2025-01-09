package org.example.backend.IPmanagement.controller;

import org.example.backend.IPmanagement.model.IpRule;
import org.example.backend.IPmanagement.Service.IpManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ip-rules")
public class IpRuleController {

    @Autowired
    private IpManagerService ipManagerService;

    @GetMapping
    public ResponseEntity<List<IpRule>> getAllRules() {
        return ResponseEntity.ok(ipManagerService.getAllIpRules());
    }

    @PostMapping
    public ResponseEntity<IpRule> addRule(@RequestBody IpRule rule) {
        return ResponseEntity.ok(ipManagerService.addIpRule(rule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable String id) {
        ipManagerService.deleteIpRule(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<IpRule> updateRule(@PathVariable String id, @RequestBody IpRule rule) {
        return ResponseEntity.ok(ipManagerService.updateIpRule(id, rule));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<IpRule>> batchAddRules(@RequestBody List<IpRule> rules) {
        return ResponseEntity.ok(ipManagerService.batchAddIpRules(rules));
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteRules(@RequestBody List<String> ruleIds) {
        ipManagerService.batchDeleteIpRules(ruleIds);
        return ResponseEntity.ok().build();
    }
} 