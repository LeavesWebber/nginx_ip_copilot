package org.example.backend.IPmanagement.model;

import java.time.LocalDateTime;

public class IpRule {
    private String id;
    private String ip;
    private String type;  // single_ip 或 ip_range
    private String comment;
    private LocalDateTime createdAt;
    private String status;
    private String ipRange;  // 用于IP范围类型的规则

    public IpRule() {
        this.createdAt = LocalDateTime.now();
        this.status = "active";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpRange() {
        return ipRange;
    }

    public void setIpRange(String ipRange) {
        this.ipRange = ipRange;
    }
}


