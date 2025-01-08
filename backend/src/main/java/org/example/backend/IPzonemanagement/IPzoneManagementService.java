package org.example.backend.IPzonemanagement;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class IPzoneManagementService {

    @Value("${blocked_countries.config.path}")
    private String configFilePath;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public GeoRule addGeoRule(GeoRule geoRule) {
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
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(configFilePath))) {
                Query query = entityManager.createQuery(
                        "SELECT g.country_code FROM GeoRule g WHERE g.status = :status",
                        String.class
                );
                // 直接传递枚举实例，而不是其字符串表示
                query.setParameter("status", GeoRule.Status.active);
                List<String> activeCountryCodes = query.getResultList();

                for (String countryCode : activeCountryCodes) {
                    writer.write(countryCode + " 1;" + System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}