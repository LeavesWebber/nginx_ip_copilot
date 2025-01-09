package org.example.backend.IPmanagement.Service;

import org.example.backend.common.NginxConfig;
import org.example.backend.IPmanagement.model.IpRule;
import org.example.backend.IPmanagement.repository.IpRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IpManagerService {
    private static final Logger logger = LoggerFactory.getLogger(IpManagerService.class);

    @Autowired
    private NginxConfig nginxConfig;

    @Autowired
    private IpRuleRepository ipRuleRepository;

    @Autowired
    private org.example.backend.common.NginxConfigService nginxConfigService;

    @Value("${nginx.block-ips-file:block_ips.conf}")
    private String blockIpsFileName;

    @Value("${nginx.reload.enabled:false}")
    private boolean nginxReloadEnabled;

    private Path getBlockIpsFilePath() {
        String nginxConfigPath = nginxConfig.getNginxConfigPath();
        if (nginxConfigPath == null || nginxConfigPath.isEmpty()) {
            throw new IllegalStateException("Nginx配置文件路径未设置");
        }

        // 获取nginx配置文件的父目录
        File nginxConfigFile = new File(nginxConfigPath);
        if (!nginxConfigFile.exists()) {
            throw new RuntimeException("Nginx配置文件不存在: " + nginxConfigPath);
        }

        File parentDir = nginxConfigFile.getParentFile();
        if (parentDir == null) {
            throw new RuntimeException("无法获取配置文件的父目录: " + nginxConfigPath);
        }
        
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new RuntimeException("无法创建配置文件目录: " + parentDir.getAbsolutePath());
            }
        }
        
        if (!parentDir.canWrite()) {
            throw new RuntimeException("配置文件目录无写入权限: " + parentDir.getAbsolutePath());
        }
        
        Path path = Paths.get(parentDir.getAbsolutePath(), "block_ips.conf");
        logger.info("生成的IP规则配置文件路径: {}", path.toString());
        return path;
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional(readOnly = true)
    public List<IpRule> getAllIpRules() {
        return ipRuleRepository.findAll();
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public IpRule addIpRule(IpRule rule) {
        // 验证IP格式
        if (!isValidIpOrRange(rule)) {
            throw new IllegalArgumentException("无效的IP地址或IP范围格式");
        }

        rule.setCreatedAt(LocalDateTime.now());
        rule.setStatus(IpRule.Status.active);
        
        IpRule savedRule = ipRuleRepository.save(rule);
        try {
            updateNginxConfig();
        } catch (Exception e) {
            // 如果更新配置文件失败，回滚数据库事务
            throw new RuntimeException("规则已保存到数据库，但更新Nginx配置文件失败: " + e.getMessage(), e);
        }
        return savedRule;
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void deleteIpRule(String ruleId) {
        ipRuleRepository.deleteById(ruleId);
        try {
            updateNginxConfig();
        } catch (Exception e) {
            throw new RuntimeException("规则已从数据库删除，但更新Nginx配置文件失败: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public IpRule updateIpRule(String ruleId, IpRule updatedRule) {
        if (!isValidIpOrRange(updatedRule)) {
            throw new IllegalArgumentException("无效的IP地址或IP范围格式");
        }

        IpRule existingRule = ipRuleRepository.findById(ruleId)
            .orElseThrow(() -> new IllegalArgumentException("规则不存在"));

        existingRule.setType(updatedRule.getType());
        existingRule.setIp(updatedRule.getIp());
        existingRule.setIpRange(updatedRule.getIpRange());
        existingRule.setComment(updatedRule.getComment());
        existingRule.setStatus(updatedRule.getStatus());

        IpRule savedRule = ipRuleRepository.save(existingRule);
        try {
            updateNginxConfig();
        } catch (Exception e) {
            throw new RuntimeException("规则已更新到数据库，但更新Nginx配置文件失败: " + e.getMessage(), e);
        }
        return savedRule;
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public List<IpRule> batchAddIpRules(List<IpRule> rules) {
        List<IpRule> validRules = rules.stream()
            .filter(this::isValidIpOrRange)
            .peek(rule -> {
                rule.setCreatedAt(LocalDateTime.now());
                rule.setStatus(IpRule.Status.active);
            })
            .collect(Collectors.toList());

        List<IpRule> savedRules = ipRuleRepository.saveAll(validRules);
        updateNginxConfig();
        return savedRules;
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void batchDeleteIpRules(List<String> ruleIds) {
        ipRuleRepository.deleteAllById(ruleIds);
        updateNginxConfig();
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
            Path blockIpsPath = getBlockIpsFilePath();
            logger.info("开始更新IP规则配置文件: {}", blockIpsPath);

            List<IpRule> rules = ipRuleRepository.findAll();
            List<String> configLines = new ArrayList<>();
            
            // 添加配置文件头部注释
            configLines.add("# IP规则配置文件 - 由系统自动生成");
            configLines.add("# 最后更新时间: " + LocalDateTime.now());
            configLines.add("");

            // 添加每条规则
            for (IpRule rule : rules) {
                if (rule.getStatus() == IpRule.Status.active) {
                    configLines.add("# " + rule.getComment());
                    if ("single_ip".equals(rule.getType())) {
                        configLines.add("deny " + rule.getIp() + ";");
                    } else if ("ip_range".equals(rule.getType())) {
                        configLines.add("deny " + rule.getIpRange() + ";");
                    }
                    configLines.add("");
                }
            }

            // 写入配置文件
            try {
                Files.write(blockIpsPath, configLines, StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                logger.info("IP规则配置文件写入成功");
            } catch (AccessDeniedException e) {
                logger.error("无法写入IP规则配置文件: {}", e.getMessage());
                throw new RuntimeException("无法写入IP规则配置文件，请检查文件权限: " + blockIpsPath, e);
            }

            // 使用NginxConfigService确保配置文件被包含
            try {
                nginxConfigService.ensureConfigIncluded(blockIpsPath.toString());
                logger.info("成功更新Nginx配置文件，确保包含了IP规则文件");
            } catch (Exception e) {
                logger.error("更新Nginx配置文件失败: {}", e.getMessage());
                throw new RuntimeException("更新Nginx配置文件失败: " + e.getMessage(), e);
            }

            // 仅在启用时尝试重新加载Nginx配置
            if (nginxReloadEnabled) {
                try {
                    nginxConfigService.reloadNginx();
                    logger.info("成功重新加载Nginx配置");
                } catch (Exception e) {
                    logger.warn("重新加载Nginx配置失败（不影响规则更新）: {}", e.getMessage());
                }
            } else {
                logger.info("Nginx重载功能已禁用，跳过重载步骤");
            }
        } catch (IOException e) {
            logger.error("更新IP规则配置时发生错误: {}", e.getMessage());
            throw new RuntimeException("更新IP规则配置时发生错误: " + e.getMessage(), e);
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