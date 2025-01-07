package org.example.backned.IPmanagement.Controller;

import org.example.backned.IPmanagement.Service.IpManagerService;
import org.example.backned.IPmanagement.model.IpRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ip-rules")
public class IpManagerController {

    @Autowired
    private IpManagerService ipManagerService;

    @PostMapping
    public String addIpRule(@RequestBody IpRule rule) {
        boolean isAdded = ipManagerService.addIpRule(rule);
        return isAdded ? "IP rule added successfully" : "Failed to add IP rule";
    }

    @DeleteMapping("/{ruleId}")
    public String deleteIpRule(@PathVariable String ruleId) {
        boolean isDeleted = ipManagerService.deleteIpRule(ruleId);
        return isDeleted ? "IP rule deleted successfully" : "Failed to delete IP rule";
    }
}