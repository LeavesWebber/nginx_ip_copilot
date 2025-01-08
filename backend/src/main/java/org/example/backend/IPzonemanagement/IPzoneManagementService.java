package org.example.backend.IPzonemanagement;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class IPzoneManagementService {

    @Value("${blocked_countries.config.path}")
    private String configFilePath;

    @PersistenceContext
    private EntityManager entityManager;

    private Path getConfigPath() {
        // 获取nginx配置文件的父目录
        File nginxConfigFile = new File(configFilePath);
        File parentDir = nginxConfigFile.getParentFile();
        
        // 在同一目录下创建blocked_countries.conf
        return Paths.get(parentDir.getAbsolutePath(), "blocked_countries.conf");
    }

    @Transactional
    public GeoRule addGeoRule(GeoRule geoRule) {
        if (geoRule.getStatus() == null) {
            geoRule.setStatus(GeoRule.Status.active);  // 设置默认状态为active
        }
        entityManager.persist(geoRule);
        saveGeoRules();
        return geoRule;
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
            Files.createDirectories(configPath.getParent());
            
            // 写入配置文件
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                // 获取所有活跃状态的规则
                Query query = entityManager.createQuery(
                    "SELECT g FROM GeoRule g WHERE g.status = :status",
                    GeoRule.class
                );
                // 直接传递枚举实例，而不是其字符串表示
                query.setParameter("status", GeoRule.Status.active);
                List<GeoRule> activeRules = query.getResultList();

                // 写入所有活跃规则
                for (GeoRule rule : activeRules) {
                    writer.write(String.format("# Rule ID: %s - %s%n", rule.getId(), rule.getComment()));
                    writer.write(rule.getCountry_code() + " 1;" + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save geo rules to " + getConfigPath(), e);
        }
    }
}