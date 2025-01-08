package org.example.backend.IPmanagement.model;

public class IpRule {
    private String id;
    private String ip;
    private String type;
    private String comment;
    private String createdAt;
    private String status;

    public IpRule(String id, String ip, String type, String comment, String createdAt, String status) {
        this.id = id;
        this.ip = ip;
        this.type = type;
        this.comment = comment;
        this.createdAt = createdAt;
        this.status = status;
    }


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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


