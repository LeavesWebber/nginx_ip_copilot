package org.example.backend.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NginxConfig {

    @Value("${nginx.config.path}")
    private String nginxConfigPath;


   @Value("${nginx.config.geo.path}")
    private String nginxGeoConfigPath;


    public String getNginxConfigPath() {
        return nginxConfigPath;
    }
    public String getNginxGeoConfigPath() {
        return nginxGeoConfigPath;
    }
}

