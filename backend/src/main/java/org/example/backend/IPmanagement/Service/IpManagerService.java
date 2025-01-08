package org.example.backend.IPmanagement.Service;

import org.example.backend.common.NginxConfig;
import org.example.backend.common.SecurityConfig;
import org.example.backend.IPmanagement.model.IpRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IpManagerService {

    @Autowired
    private NginxConfig nginxConfig;
    
    private final String BLOCK_IPS_FILE = "block_ips.conf";
    private List<IpRule> ipRules = new ArrayList<>();

    @PreAuthorize("hasRole('USER')")
    public List<IpRule> getAllIpRules() {
        loadRulesFromFile();
        return new ArrayList<>(ipRules);
    }

    @PreAuthorize("hasRole('USER')")
    public IpRule addIpRule(IpRule rule) {
        loadRulesFromFile();
        rule.setId(UUID.randomUUID().toString());
        rule.setCreatedAt(LocalDateTime.now());
        rule.setStatus("active");
        
        // 验证IP格式
        if (!isValidIpOrRange(rule)) {
            throw new IllegalArgumentException("无效的IP地址或IP范围格式");
        }

        ipRules.add(rule);
        saveRulesToFile();
        updateNginxConfig();
        return rule;
    }

    @PreAuthorize("hasRole('USER')")
    public boolean deleteIpRule(String ruleId) {
        loadRulesFromFile();
        boolean removed = ipRules.removeIf(rule -> rule.getId().equals(ruleId));
        if (removed) {
            saveRulesToFile();
            updateNginxConfig();
        }
        return removed;
    }

    @PreAuthorize("hasRole('USER')")
    public IpRule updateIpRule(String ruleId, IpRule updatedRule) {
        loadRulesFromFile();
        for (int i = 0; i < ipRules.size(); i++) {
            if (ipRules.get(i).getId().equals(ruleId)) {
                updatedRule.setId(ruleId);
                updatedRule.setCreatedAt(ipRules.get(i).getCreatedAt());
                if (!isValidIpOrRange(updatedRule)) {
                    throw new IllegalArgumentException("无效的IP地址或IP范围格式");
                }
                ipRules.set(i, updatedRule);
                saveRulesToFile();
                updateNginxConfig();
                return updatedRule;
            }
        }
        return null;
    }

    @PreAuthorize("hasRole('USER')")
    public List<IpRule> batchAddIpRules(List<IpRule> rules) {
        loadRulesFromFile();
        List<IpRule> addedRules = new ArrayList<>();
        for (IpRule rule : rules) {
            rule.setId(UUID.randomUUID().toString());
            rule.setCreatedAt(LocalDateTime.now());
            rule.setStatus("active");
            if (isValidIpOrRange(rule)) {
                ipRules.add(rule);
                addedRules.add(rule);
            }
        }
        if (!addedRules.isEmpty()) {
            saveRulesToFile();
            updateNginxConfig();
        }
        return addedRules;
    }

    @PreAuthorize("hasRole('USER')")
    public boolean batchDeleteIpRules(List<String> ruleIds) {
        loadRulesFromFile();
        boolean anyRemoved = false;
        for (String ruleId : ruleIds) {
            if (ipRules.removeIf(rule -> rule.getId().equals(ruleId))) {
                anyRemoved = true;
            }
        }
        if (anyRemoved) {
            saveRulesToFile();
            updateNginxConfig();
        }
        return anyRemoved;
    }

    private void loadRulesFromFile() {
        try {
            Path filePath = Paths.get(BLOCK_IPS_FILE);
            if (!Files.exists(filePath)) {
                ipRules = new ArrayList<>();
                return;
            }
            
            List<String> lines = Files.readAllLines(filePath);
            ipRules = lines.stream()
                .filter(line -> line.startsWith("# RULE:"))
                .map(this::parseRuleLine)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("无法读取规则文件", e);
        }
    }

    private void saveRulesToFile() {
        try {
            List<String> lines = new ArrayList<>();
            for (IpRule rule : ipRules) {
                lines.add(formatRuleLine(rule));
                if (rule.getType().equals("single_ip")) {
                    lines.add("deny " + rule.getIp() + ";");
                } else if (rule.getType().equals("ip_range")) {
                    lines.add("deny " + rule.getIpRange() + ";");
                }
            }
            Files.write(Paths.get(BLOCK_IPS_FILE), lines);
        } catch (IOException e) {
            throw new RuntimeException("无法保存规则文件", e);
        }
    }

    private String formatRuleLine(IpRule rule) {
        return String.format("# RULE: {\"id\":\"%s\",\"type\":\"%s\",\"ip\":\"%s\",\"ipRange\":\"%s\",\"comment\":\"%s\",\"createdAt\":\"%s\",\"status\":\"%s\"}",
            rule.getId(), rule.getType(), rule.getIp(), rule.getIpRange(), rule.getComment(), rule.getCreatedAt(), rule.getStatus());
    }

    private IpRule parseRuleLine(String line) {
        try {
            String json = line.substring(line.indexOf("{"));
            // 这里需要添加JSON解析逻辑，可以使用Jackson或Gson
            // 为简化示例，这里省略具体实现
            return new IpRule(); // 实际应该返回解析后的对象
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isValidIpOrRange(IpRule rule) {
        if ("single_ip".equals(rule.getType())) {
            return isValidIpv4(rule.getIp());
        } else if ("ip_range".equals(rule.getType())) {
            return isValidIpRange(rule.getIpRange());
        }
        return false;
    }

    private boolean isValidIpv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        try {
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidIpRange(String ipRange) {
        if (ipRange == null || ipRange.isEmpty()) {
            return false;
        }
        String[] parts = ipRange.split("/");
        if (parts.length != 2) {
            return false;
        }
        try {
            if (!isValidIpv4(parts[0])) {
                return false;
            }
            int maskBits = Integer.parseInt(parts[1]);
            return maskBits >= 0 && maskBits <= 32;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateNginxConfig() {
        try {
            String nginxConfigPath = nginxConfig.getNginxConfigPath();
            List<String> configLines = Files.readAllLines(Paths.get(nginxConfigPath));
            
            // 检查是否已包含include语句
            boolean hasInclude = configLines.stream()
                .anyMatch(line -> line.trim().equals("include " + BLOCK_IPS_FILE + ";"));
            
            if (!hasInclude) {
                // 在http块内添加include语句
                int httpIndex = configLines.indexOf("http {");
                if (httpIndex != -1) {
                    configLines.add(httpIndex + 1, "    include " + BLOCK_IPS_FILE + ";");
                    Files.write(Paths.get(nginxConfigPath), configLines);
                }
            }
            
            // 重新加载Nginx配置
            reloadNginx();
        } catch (IOException e) {
            throw new RuntimeException("无法更新Nginx配置", e);
        }
    }

    @PreAuthorize("hasRole('USER')")
    public boolean reloadNginx() {
        try {
            Process process = Runtime.getRuntime().exec("nginx -s reload");
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("无法重新加载Nginx", e);
        }
    }
}