package com.nordstrom.hydrogen.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatadogConfig {

    @Value("${DD_API_KEY}")
    private String apiKey;

    @Value("${DD_APPLICATION_KEY}")
    private String applicationKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }


}
