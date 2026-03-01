package com.decisiontool.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "denodo")
public class DenodoConfig {

    private String baseUrl;
    private String username;
    private String password;
    private String vdpDatabaseName;
    private int timeoutSeconds;
    private int maxRetries;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getVdpDatabaseName() { return vdpDatabaseName; }
    public void setVdpDatabaseName(String vdpDatabaseName) { this.vdpDatabaseName = vdpDatabaseName; }

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
}