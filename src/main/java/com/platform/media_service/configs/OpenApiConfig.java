package com.platform.media_service.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class for OpenAPI settings.
 */
@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    /**
     * Configures a custom {@link MappingJackson2HttpMessageConverter}
     * to handle requests and responses
     * with the media type 'application/octet-stream'.
     *
     * @return {@link MappingJackson2HttpMessageConverter}
     * A message converter supporting 'application/octet-stream'.
     */
    @Bean
    public MappingJackson2HttpMessageConverter octetStreamJsonConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // Set supported media type to 'application/octet-stream'
        converter.setSupportedMediaTypes(List.of(new MediaType("application", "octet-stream")));
        return converter;
    }

    /**
     * Configures content negotiation to use 'application/json'
     * as the default content type.
     *
     * @param configurer {@link ContentNegotiationConfigurer}
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Set 'application/json' as the default content type if none is specified
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
