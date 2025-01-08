package org.example.backend.IPmanagement.Service;

import org.example.backend.common.NginxConfig;
import org.example.backend.common.SecurityConfig;
import org.example.backend.IPmanagement.model.IpRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IpManagerService {

    @Autowired
    private NginxConfig NginxConfig;
    
    @PreAuthorize("hasRole('USER')")
    public boolean addIpRule(IpRule rule) {
        String nginxConfigPath = NginxConfig.getNginxConfigPath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nginxConfigPath, true))) {
            writer.write("deny " + rule.getIp() + ";\n");
            writer.flush();
            return reloadNginx();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @PreAuthorize("hasRole('USER')")
    public boolean deleteIpRule(String ruleId) {
        String nginxConfigPath = NginxConfig.getNginxConfigPath();
        try {
            List<String> lines = Files.readAllLines(Paths.get(nginxConfigPath));
            lines.removeIf(line -> line.equals("deny " + ruleId + ";"));
            Files.write(Paths.get(nginxConfigPath), lines);
            return reloadNginx();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @PreAuthorize("hasRole('USER')")
    public boolean reloadNginx() {
        try {
            Process process = Runtime.getRuntime().exec("nginx -s reload");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}