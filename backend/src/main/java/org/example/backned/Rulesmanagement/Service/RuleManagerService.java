package org.example.backned.Rulesmanagement.Service;

import org.example.backned.Rulesmanagement.model.Rule;
import org.example.backned.common.NginxConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RuleManagerService {

    @Autowired
    private NginxConfig ruleNginxConfig;

    @PreAuthorize("hasRole('USER')")
    public List<Rule> getRules() {
        List<Rule> rules = new ArrayList<>();
        rules.addAll(getIpRules());
        rules.addAll(getGeoRules());
        return rules;
    }

    @PreAuthorize("hasRole('USER')")
    public List<Rule> getIpRules() {
        String nginxIpConfigPath = ruleNginxConfig.getNginxConfigPath();
        List<Rule> ipRules = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(nginxIpConfigPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("deny ")) {
                    String ip = line.substring(5, line.length() - 1);
                    ipRules.add(new Rule("IP", ip));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ipRules;
    }

    @PreAuthorize("hasRole('USER')")
    public List<Rule> getGeoRules() {
        String nginxGeoConfigPath = ruleNginxConfig.getNginxGeoConfigPath();
        List<Rule> geoRules = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(nginxGeoConfigPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("geoip_country ")) {
                    String countryCode = line.substring(14, line.length() - 1);
                    geoRules.add(new Rule("GeoIP", countryCode));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return geoRules;
    }
}