package com.amrit.stockpriceservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlphaVantageConfig {
    @Value("${alphavantage.api.url}")
    private String apiUrl;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }
}
