package org.example.backend.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.nio.file.StandardOpenOption;

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
            Files.writeString(path, content, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
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
        
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            boolean isWindows = osName.contains("win");
            String command;
            
            if (isWindows) {
                // Windows 环境
                String nginxPath = System.getenv("NGINX_HOME");
                if (nginxPath == null || nginxPath.trim().isEmpty()) {
                    logger.warn("NGINX_HOME environment variable is not set");
                    status.put("status", "stopped");
                    status.put("worker_processes", 0);
                    status.put("connections", 0);
                    status.put("config", readConfig());
                    return status;
                }
                command = "tasklist /FI \"IMAGENAME eq nginx.exe\" /FO CSV /NH";
            } else {
                // Linux/Unix 环境
                command = "ps aux | grep nginx | grep -v grep";
            }
            
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean isRunning = false;
            int workerCount = 0;
            
            while ((line = reader.readLine()) != null) {
                if (isWindows) {
                    if (line.toLowerCase().contains("nginx.exe")) {
                        isRunning = true;
                        workerCount++;
                    }
                } else {
                    if (line.contains("nginx: worker process")) {
                        workerCount++;
                    } else if (line.contains("nginx: master process")) {
                        isRunning = true;
                    }
                }
            }
            
            process.waitFor(5, TimeUnit.SECONDS);
            
            status.put("status", isRunning ? "running" : "stopped");
            status.put("worker_processes", workerCount);
            status.put("connections", isRunning ? getActiveConnections() : 0);
            status.put("config", readConfig());
            
        } catch (Exception e) {
            logger.error("Error checking nginx status: " + e.getMessage());
            status.put("status", "stopped");
            status.put("worker_processes", 0);
            status.put("connections", 0);
            try {
                status.put("config", readConfig());
            } catch (Exception ex) {
                status.put("config", "");
            }
        }
        
        return status;
    }
    
    private int getActiveConnections() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
                // Windows 环境下使用 netstat 命令
                Process process = Runtime.getRuntime().exec("netstat -an | findstr :80");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                int connections = 0;
                while (reader.readLine() != null) {
                    connections++;
                }
                return connections;
            } else {
                // Linux/Unix 环境可以通过读取 nginx status 页面获取
                return 0; // 这里可以实现通过访问 nginx status 页面获取连接数
            }
        } catch (Exception e) {
            logger.error("Error getting active connections: " + e.getMessage());
            return 0;
        }
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

    public Map<String, Object> checkGeoModuleStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("isInstalled", false);
        result.put("isEnabled", false);
        result.put("message", "");

        try {
            String configPath = nginxConfig.getNginxConfigPath();
            if (configPath == null || configPath.isEmpty()) {
                result.put("message", "未设置Nginx配置文件路径");
                return result;
            }

            // 检查配置文件是否存在
            Path path = Paths.get(configPath);
            if (!Files.exists(path)) {
                result.put("message", "Nginx配置文件不存在");
                return result;
            }

            // 检查是否有load_module语句
            List<String> lines = Files.readAllLines(path);
            boolean hasLoadModule = false;
            boolean isEnabled = false;

            for (String line : lines) {
                String trimmedLine = line.trim();
                // 检查是否有加载模块的语句
                if (trimmedLine.startsWith("load_module") && trimmedLine.contains("ngx_http_geoip2_module")) {
                    hasLoadModule = true;
                }
                // 检查是否有geoip2配置块
                if (trimmedLine.startsWith("geoip2") && trimmedLine.contains(".mmdb")) {
                    isEnabled = true;
                }
            }

            result.put("isInstalled", hasLoadModule);
            result.put("isEnabled", isEnabled);
            
            if (!hasLoadModule) {
                result.put("message", "GeoIP2模块未安装");
            } else if (!isEnabled) {
                result.put("message", "GeoIP2模块已安装但未启用");
            } else {
                result.put("message", "GeoIP2模块已安装并启用");
            }
            
        } catch (Exception e) {
            logger.error("检查GeoIP2模块状态失败", e);
            result.put("message", "检查GeoIP2模块状态失败: " + e.getMessage());
        }
        
        return result;
    }

    private boolean checkGeoModuleEnabled() {
        String configPath = nginxConfig.getNginxConfigPath();
        if (configPath == null || configPath.isEmpty()) {
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(configPath));
            for (String line : lines) {
                if (line.contains("load_module") && line.contains("ngx_http_geoip2_module")) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error("检查GeoIP2模块配置失败", e);
        }
        return false;
    }

    public Map<String, Object> installGeoModule() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "");

        try {
            // 这里需要实现具体的安装逻辑
            // 1. 下载并编译GeoIP2模块
            // 2. 修改nginx配置文件
            // 3. 重启nginx服务
            
            // 示例实现（实际需要根据不同操作系统和环境调整）
            ProcessBuilder pb = new ProcessBuilder(
                "bash",
                "-c",
                "cd /tmp && " +
                "git clone https://github.com/leev/ngx_http_geoip2_module.git && " +
                "cd ngx_http_geoip2_module && " +
                "nginx -V 2>&1 | grep 'configure arguments:' | sed 's/configure arguments: //g' | " +
                "xargs -I{} nginx -V --add-dynamic-module=/tmp/ngx_http_geoip2_module {}"
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            if (process.waitFor() == 0) {
                // 安装成功，修改nginx配置
                updateNginxConfig();
                result.put("success", true);
                result.put("message", "GeoIP2模块安装成功");
            } else {
                result.put("message", "GeoIP2模块安装失败: " + output.toString());
            }
            
        } catch (Exception e) {
            logger.error("安装GeoIP2模块失败", e);
            result.put("message", "安装GeoIP2模块失败: " + e.getMessage());
        }
        
        return result;
    }

    public void ensureConfigIncluded(String geoRulesPath) throws IOException {
        String configPath = nginxConfig.getNginxConfigPath();
        if (configPath == null || configPath.isEmpty()) {
            throw new RuntimeException("Nginx配置文件路径未设置");
        }

        logger.info("开始处理配置文件包含关系");
        logger.info("Nginx配置文件路径: {}", configPath);
        logger.info("地理规则文件路径: {}", geoRulesPath);

        // 获取路径
        Path nginxConfigPath = Paths.get(configPath).toAbsolutePath().normalize();
        Path geoRulesConfigPath = Paths.get(geoRulesPath).toAbsolutePath().normalize();
        
        // 计算相对路径
        Path relativePath;
        try {
            relativePath = nginxConfigPath.getParent().relativize(geoRulesConfigPath);
            logger.info("计算得到的相对路径: {}", relativePath);
        } catch (Exception e) {
            logger.error("计算相对路径失败，将使用绝对路径", e);
            relativePath = geoRulesConfigPath;
        }
        
        // 统一使用正斜杠
        String includePathStr = relativePath.toString().replace('\\', '/');
        logger.info("最终使用的include路径: {}", includePathStr);

        // 读取现有配置
        List<String> lines = Files.readAllLines(nginxConfigPath);
        boolean includeExists = false;
        boolean geoipConfigExists = false;
        int serverBlockStart = -1;
        int serverBlockEnd = -1;

        // 遍历配置文件
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            
            // 检查是否已包含规则文件
            if (line.startsWith("include") && line.contains(includePathStr)) {
                includeExists = true;
                logger.info("找到已存在的include语句，在第{}行", i + 1);
            }
            
            // 检查是否已存在GeoIP2配置
            if (line.startsWith("geoip2") && line.contains("GeoLite2-Country.mmdb")) {
                geoipConfigExists = true;
                logger.info("找到已存在的GeoIP2配置，在第{}行", i + 1);
            }

            // 记录server块的位置
            if (line.startsWith("server {")) {
                serverBlockStart = i;
                logger.info("找到server块开始位置，在第{}行", i + 1);
            } else if (serverBlockStart != -1 && line.equals("}")) {
                serverBlockEnd = i;
                logger.info("找到server块结束位置，在第{}行", i + 1);
                break;
            }
        }

        // 如果没有include语句且找到了server块，添加必要的配置
        if (!includeExists && serverBlockStart != -1) {
            logger.info("未找到include语句，准备添加到server块中");
            
            // 获取server块的缩进
            String serverLine = lines.get(serverBlockStart);
            String indent = serverLine.substring(0, serverLine.indexOf("server"));
            logger.info("使用的缩进: '{}'", indent);
            
            // 在server块内部添加include语句（位置在server块开始的下一行）
            List<String> newLines = new ArrayList<>();
            
            // 添加到server块开始之后
            newLines.addAll(lines.subList(0, serverBlockStart + 1));
            
            // 只有在GeoIP2配置不存在时才添加
            if (!geoipConfigExists) {
                newLines.add("");
                newLines.add(indent + "    # GeoIP2 Configuration");
                newLines.add(indent + "    geoip2 /usr/share/GeoIP/GeoLite2-Country.mmdb {");
                newLines.add(indent + "        auto_reload 5m;");
                newLines.add(indent + "        $geoip2_country_code country iso_code;");
                newLines.add(indent + "    }");
                newLines.add("");
            }
            
            newLines.add(indent + "    # Include GeoIP blocking rules");
            newLines.add(indent + "    include " + includePathStr + ";");
            newLines.add("");
            
            // 添加剩余的配置
            newLines.addAll(lines.subList(serverBlockStart + 1, lines.size()));
            
            // 写回配置文件
            Files.write(nginxConfigPath, newLines);
            logger.info("成功更新Nginx配置文件，添加了include语句");
        } else if (!includeExists) {
            logger.error("未找到server块，无法添加include语句");
            throw new RuntimeException("Nginx配置文件中未找到server块，无法添加include语句");
        } else {
            logger.info("include语句已存在，无需修改");
        }
    }

    private void updateNginxConfig() throws IOException {
        String configPath = nginxConfig.getNginxConfigPath();
        if (configPath == null || configPath.isEmpty()) {
            throw new RuntimeException("Nginx配置文件路径未设置");
        }

        // 读取现有配置
        Path path = Paths.get(configPath);
        List<String> lines = Files.readAllLines(path);
        boolean moduleLoaded = false;
        
        // 检查模块加载
        for (String line : lines) {
            if (line.contains("load_module") && line.contains("ngx_http_geoip2_module")) {
                moduleLoaded = true;
                break;
            }
        }
        
        // 如果模块未加载，添加加载语句
        if (!moduleLoaded) {
            List<String> newLines = new ArrayList<>();
            newLines.add("load_module modules/ngx_http_geoip2_module.so;");
            newLines.add("");
            newLines.addAll(lines);
            
            // 写回配置文件
            Files.write(path, newLines);
            logger.info("成功更新Nginx配置文件，添加了模块加载语句");
        }
    }
} 