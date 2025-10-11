package com.platform.media_service.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration class for RestTemplate settings.
 */
@Configuration
public class RestTemplateConfig {
    /**
     * Timeout duration for the RestTemplate in milliseconds.
     */
    private static final int TIMEOUT = 1000;

    /**
     * Creates a RestTemplate bean with custom timeout settings.
     *
     * @param builder the RestTemplateBuilder used to configure the RestTemplate
     * @return a RestTemplate instance with the specified timeout settings
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(TIMEOUT))
                .setReadTimeout(Duration.ofMillis(TIMEOUT))
                .build();
    }
}
