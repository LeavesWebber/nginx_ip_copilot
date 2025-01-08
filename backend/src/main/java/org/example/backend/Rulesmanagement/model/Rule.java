package org.example.backend.Rulesmanagement.model;

public class Rule {
    private String type; // IP 或 GeoIP
    private String value; // IP 地址或地理位置代码

    public Rule(String type, String value) {
        this.type = type;
        this.value = value;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}