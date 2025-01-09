package org.example.backend.IPzonemanagement;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.backend.common.NginxConfig;
import org.example.backend.common.NginxConfigService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IPzoneManagementService {
    private static final Logger logger = LoggerFactory.getLogger(IPzoneManagementService.class);

    @Autowired
    private NginxConfig nginxConfig;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NginxConfigService nginxConfigService;

    private Path getConfigPath() {
        String nginxConfigPath = nginxConfig.getNginxConfigPath();
        logger.info("Nginx配置文件路径: {}", nginxConfigPath);
        
        if (nginxConfigPath == null || nginxConfigPath.trim().isEmpty()) {
            throw new RuntimeException("Nginx配置文件路径未设置，请先在设置页面配置Nginx路径");
        }

        // 获取nginx配置文件的父目录
        File nginxConfigFile = new File(nginxConfigPath);
        if (!nginxConfigFile.exists()) {
            throw new RuntimeException("Nginx配置文件不存在: " + nginxConfigPath);
        }

        File parentDir = nginxConfigFile.getParentFile();
        if (!parentDir.exists() || !parentDir.canWrite()) {
            throw new RuntimeException("Nginx配置文件目录不存在或无写入权限: " + parentDir.getAbsolutePath());
        }
        
        Path path = Paths.get(parentDir.getAbsolutePath(), "blocked_countries.conf");
        logger.info("生成的配置文件路径: {}", path.toString());
        return path;
    }

    @Transactional
    public GeoRule addGeoRule(GeoRule geoRule) {
        if (geoRule.getStatus() == null) {
            geoRule.setStatus(GeoRule.Status.active);
        }
        try {
            entityManager.persist(geoRule);
            saveGeoRules();
            return geoRule;
        } catch (RuntimeException e) {
            logger.error("添加地理规则失败", e);
            throw e;
        }
    }

    @Transactional
    public void deleteGeoRule(String ruleId) {
        GeoRule rule = entityManager.find(GeoRule.class, ruleId);
        if (rule != null) {
            entityManager.remove(rule);
            saveGeoRules();
        }
    }

    public List<GeoRule> getAllGeoRules() {
        Query query = entityManager.createQuery("SELECT g FROM GeoRule g", GeoRule.class);
        return query.getResultList();
    }

    public void saveGeoRules() {
        try {
            // 确保父目录存在
            Path configPath = getConfigPath();
            logger.info("创建目录: {}", configPath.getParent());
            Files.createDirectories(configPath.getParent());
            
            // 获取所有活跃状态的规则
            Query query = entityManager.createQuery(
                "SELECT g FROM GeoRule g WHERE g.status = :status",
                GeoRule.class
            );
            query.setParameter("status", GeoRule.Status.active);
            List<GeoRule> activeRules = query.getResultList();
            logger.info("找到 {} 条活跃规则", activeRules.size());

            // 写入配置文件
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                // 写入文件头部注释
                writer.write("# Auto-generated GeoIP2 rules - DO NOT EDIT MANUALLY\n");
                writer.write("# Generated at: " + LocalDateTime.now() + "\n\n");

                // 写入规则
                for (GeoRule rule : activeRules) {
                    // 添加规则注释
                    writer.write(String.format("# Rule ID: %s - %s\n", rule.getId(), rule.getComment()));
                    // 写入if条件语句
                    writer.write(String.format("if ($geoip2_country_code = %s) {\n", rule.getCountry_code()));
                    writer.write("    return 403;\n");
                    writer.write("}\n\n");
                }
                logger.info("配置文件写入成功: {}", configPath);
            }

            // 更新nginx配置文件，添加include语句
            try {
                nginxConfigService.ensureConfigIncluded(configPath.toString());
                // 尝试重新加载nginx
                nginxConfigService.reloadNginx();
            } catch (Exception e) {
                logger.warn("更新Nginx配置或重载时发生错误: {}", e.getMessage());
                // 不抛出异常，因为规则文件已经成功保存
            }
        } catch (IOException e) {
            logger.error("保存配置文件失败", e);
            throw new RuntimeException("Failed to save geo rules to " + getConfigPath(), e);
        }
    }
}