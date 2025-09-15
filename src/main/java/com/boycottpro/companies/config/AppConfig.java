package com.boycottpro.companies.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    
    private static final String DEFAULT_PROPERTIES_FILE = "application.properties";
    private static final String PROFILE_ENV_VAR = "APP_PROFILE";
    private static AppConfig instance;
    private final Properties properties;
    private final String activeProfile;
    
    private AppConfig() {
        properties = new Properties();
        activeProfile = determineActiveProfile();
        loadProperties();
    }
    
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    private void loadProperties() {
        // Load default properties first
        loadPropertiesFile(DEFAULT_PROPERTIES_FILE);
        
        // Load profile-specific properties if profile is set
        if (!"default".equals(activeProfile)) {
            String profilePropertiesFile = "application-" + activeProfile + ".properties";
            loadPropertiesFile(profilePropertiesFile);
        }
    }
    
    private void loadPropertiesFile(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                if (fileName.equals(DEFAULT_PROPERTIES_FILE)) {
                    throw new RuntimeException("Unable to find " + fileName + " in classpath");
                }
                // Profile-specific files are optional
                System.out.println("Profile-specific properties file not found: " + fileName + ", using defaults");
                return;
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from " + fileName, e);
        }
    }
    
    private String determineActiveProfile() {
        // Check environment variable first
        String profile = System.getenv(PROFILE_ENV_VAR);
        if (profile != null && !profile.trim().isEmpty()) {
            return profile.toLowerCase().trim();
        }
        
        // Check system property as fallback
        profile = System.getProperty("app.profile");
        if (profile != null && !profile.trim().isEmpty()) {
            return profile.toLowerCase().trim();
        }
        
        return "default";
    }
    
    public String getS3BucketName() {
        return getProperty("s3.bucket.name");
    }
    
    public String getAppName() {
        return getProperty("app.name");
    }
    
    public String getAppVersion() {
        return getProperty("app.version");
    }
    
    public String getActiveProfile() {
        return activeProfile;
    }
    
    public String getEnvironment() {
        return getProperty("app.environment");
    }
    
    public String getLogLevel() {
        return getProperty("app.log.level");
    }
    
    public boolean isCacheEnabled() {
        return Boolean.parseBoolean(getProperty("app.cache.enabled"));
    }
    
    public boolean isMetricsEnabled() {
        return Boolean.parseBoolean(getProperty("app.metrics.enabled"));
    }
    
    private String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in loaded properties. Active profile: " + activeProfile);
        }
        return value;
    }
    
    // For testing - allows injection of custom properties
    public AppConfig(Properties testProperties) {
        this.properties = testProperties;
        this.activeProfile = "test";
    }
    
    // For testing - allows injection of custom properties with profile
    public AppConfig(Properties testProperties, String profile) {
        this.properties = testProperties;
        this.activeProfile = profile;
    }
}