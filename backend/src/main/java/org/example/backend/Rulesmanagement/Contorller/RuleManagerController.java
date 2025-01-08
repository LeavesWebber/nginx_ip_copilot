package org.example.backend.Rulesmanagement.Contorller;

import org.example.backend.Rulesmanagement.model.Rule;
import org.example.backend.Rulesmanagement.Service.RuleManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rule-manager")
public class RuleManagerController {

    @Autowired
    private RuleManagerService ruleManagerService;

    @GetMapping("/rules")
    public List<Rule> getRules() {
        return ruleManagerService.getRules();
    }

    @GetMapping("/ip-rules")
    public List<Rule> getIpRules() {
        return ruleManagerService.getIpRules();
    }

    @GetMapping("/geo-rules")
    public List<Rule> getGeoRules() {
        return ruleManagerService.getGeoRules();
    }
}