package org.example.backend.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class NginxConfigService {
    private static final Logger logger = LoggerFactory.getLogger(NginxConfigService.class);

    @Autowired
    private NginxConfig nginxConfig;

    public Map<String, Object> validateConfigPath(String path) {
        Map<String, Object> result = new HashMap<>();
        result.put("exists", false);
        result.put("isFile", false);
        result.put("canRead", false);
        result.put("canWrite", false);

        if (path == null || path.trim().isEmpty()) {
            logger.warn("Path is null or empty");
            return result;
        }

        try {
            // 处理路径中的引号
            path = path.replace("\"", "").replace("'", "");
            
            // 创建File对象
            File file = new File(path);
            
            // 记录日志
            logger.info("Validating path: " + path);
            logger.info("File exists: " + file.exists());
            logger.info("Is file: " + file.isFile());
            logger.info("Can read: " + file.canRead());
            logger.info("Can write: " + file.canWrite());
            logger.info("Absolute path: " + file.getAbsolutePath());
            
            // 设置详细的验证结果
            result.put("exists", file.exists());
            result.put("isFile", file.isFile());
            result.put("canRead", file.canRead());
            result.put("canWrite", file.canWrite());
            
            return result;
            
        } catch (SecurityException e) {
            logger.error("Security exception while validating path: " + e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("Unexpected error while validating path: " + e.getMessage());
            return result;
        }
    }

    public String readConfig() {
        try {
            String configPath = nginxConfig.getNginxConfigPath();
            if (configPath == null || configPath.trim().isEmpty()) {
                throw new RuntimeException("未设置Nginx配置文件路径，请先在设置页面设置配置文件路径");
            }

            Path path = Paths.get(configPath);
            if (!Files.exists(path)) {
                throw new RuntimeException("配置文件不存在：" + configPath);
            }
            if (!Files.isReadable(path)) {
                throw new RuntimeException("无法读取配置文件，请检查权限：" + configPath);
            }

            return Files.readString(path);
        } catch (IOException e) {
            logger.error("Error reading config file: " + e.getMessage());
            throw new RuntimeException("读取配置文件时发生错误：" + e.getMessage());
        }
    }

    public void updateConfig(String content) {
        try {
            String configPath = nginxConfig.getNginxConfigPath();
            if (configPath == null || configPath.trim().isEmpty()) {
                throw new RuntimeException("未设置Nginx配置文件路径，请先在设置页面设置配置文件路径");
            }

            Path path = Paths.get(configPath);
            
            // 验证文件状态
            Map<String, Object> validation = validateConfigPath(configPath);
            if (!(boolean)validation.get("exists")) {
                throw new RuntimeException("配置文件不存在：" + configPath);
            }
            if (!(boolean)validation.get("isFile")) {
                throw new RuntimeException("指定的路径不是一个文件：" + configPath);
            }
            if (!(boolean)validation.get("canRead")) {
                throw new RuntimeException("无法读取配置文件，请检查权限：" + configPath);
            }
            if (!(boolean)validation.get("canWrite")) {
                throw new RuntimeException("无法写入配置文件，请检查权限：" + configPath);
            }

            // 创建备份
            createBackup();

            // 写入新配置
            Files.writeString(path, content);
            logger.info("Successfully updated nginx config file: " + configPath);

        } catch (IOException e) {
            logger.error("Error updating config file: " + e.getMessage());
            throw new RuntimeException("更新配置文件时发生错误：" + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while updating config: " + e.getMessage());
            throw new RuntimeException("更新配置文件时发生错误：" + e.getMessage());
        }
    }

    private void createBackup() throws IOException {
        try {
            Path sourcePath = Paths.get(nginxConfig.getNginxConfigPath());
            if (!Files.exists(sourcePath)) {
                return; // 如果源文件不存在，跳过备份
            }
            
            // 创建新的备份
            String timestamp = String.valueOf(System.currentTimeMillis());
            Path backupPath = Paths.get(sourcePath.getParent().toString(), 
                sourcePath.getFileName() + "." + timestamp + ".backup");
            Files.copy(sourcePath, backupPath);
            logger.info("Created backup file: " + backupPath);

            // 清理旧的备份文件，只保留最近的5个
            try {
                cleanupOldBackups(sourcePath.getParent(), sourcePath.getFileName().toString(), 1);
            } catch (Exception e) {
                logger.warn("Failed to cleanup old backups: " + e.getMessage());
                // 清理失败不影响主流程
            }
        } catch (IOException e) {
            logger.error("Failed to create backup: " + e.getMessage());
            throw new IOException("创建配置文件备份失败：" + e.getMessage());
        }
    }

    private void cleanupOldBackups(Path directory, String baseFileName, int keepCount) throws IOException {
        // 获取所有备份文件
        List<Path> backupFiles = Files.list(directory)
            .filter(path -> {
                String fileName = path.getFileName().toString();
                return fileName.startsWith(baseFileName) && fileName.endsWith(".backup");
            })
            .sorted((p1, p2) -> {
                // 按文件名中的时间戳倒序排序
                String ts1 = p1.getFileName().toString().replaceAll(".*\\.(\\d+)\\.backup", "$1");
                String ts2 = p2.getFileName().toString().replaceAll(".*\\.(\\d+)\\.backup", "$1");
                return ts2.compareTo(ts1);
            })
            .collect(Collectors.toList());

        // 删除多余的备份文件
        if (backupFiles.size() > keepCount) {
            for (int i = keepCount; i < backupFiles.size(); i++) {
                try {
                    Files.delete(backupFiles.get(i));
                    logger.info("Deleted old backup file: " + backupFiles.get(i));
                } catch (IOException e) {
                    logger.warn("Failed to delete old backup file: " + backupFiles.get(i));
                }
            }
        }
    }

    public Map<String, Object> getNginxStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");  // 这里可以添加实际的nginx状态检查
        status.put("worker_processes", 4); // 这里可以添加实际的worker进程数获取
        status.put("connections", 100);    // 这里可以添加实际的连接数获取
        status.put("config", readConfig());
        return status;
    }

    public boolean reloadNginx() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            String command;
            
            if (osName.contains("win")) {
                // Windows环境
                String nginxPath = System.getenv("NGINX_HOME");
                if (nginxPath == null || nginxPath.trim().isEmpty()) {
                    logger.error("NGINX_HOME environment variable is not set");
                    throw new RuntimeException("请检查 nginx 是否正常运行");
                }
                command = nginxPath + "\\nginx.exe -s reload";
            } else {
                // Linux/Unix环境
                command = "nginx -s reload";
            }
            
            logger.info("Executing nginx reload command: " + command);
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                // 读取错误输出
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String errorOutput = reader.lines().collect(Collectors.joining("\n"));
                    logger.error("Nginx reload failed with exit code " + exitCode + ": " + errorOutput);
                    throw new RuntimeException("重载Nginx失败: " + errorOutput);
                }
            }
            
            return exitCode == 0;
        } catch (IOException e) {
            logger.error("Error reloading nginx: " + e.getMessage());
            throw new RuntimeException("无法重新加载Nginx: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while reloading nginx: " + e.getMessage());
            throw new RuntimeException("重载Nginx过程被中断");
        }
    }

    public Map<String, Object> getSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("nginxConfigPath", nginxConfig.getNginxConfigPath());
        return settings;
    }

    public void updateSettings(Map<String, Object> settings) {
        if (settings.containsKey("nginxConfigPath")) {
            String newPath = (String) settings.get("nginxConfigPath");
            logger.info("Updating nginx config path to: " + newPath);
            Map<String, Object> validation = validateConfigPath(newPath);
            
            if (!(boolean)validation.get("exists")) {
                throw new RuntimeException("配置文件不存在");
            }
            if (!(boolean)validation.get("isFile")) {
                throw new RuntimeException("指定的路径不是一个文件");
            }
            if (!(boolean)validation.get("canRead")) {
                throw new RuntimeException("无法读取配置文件，请检查权限");
            }
            if (!(boolean)validation.get("canWrite")) {
                throw new RuntimeException("无法写入配置文件，请检查权限");
            }
            
            nginxConfig.setNginxConfigPath(newPath);
            logger.info("Successfully updated nginx config path");
        }
    }
} 