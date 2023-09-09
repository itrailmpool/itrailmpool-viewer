package com.itrailmpool.itrailmpoolviewer.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource(value = "classpath:${env:dev}/application.properties")
public class ApplicationConfig {

    public static final String DEFAULT_DATA_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String LOCAL_DATA_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String UTC_TIMEZONE = "UTC";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public ObjectMapper defaultObjectMapper() {
        return JsonMapper.builder()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .addModule(new JavaTimeModule())
                .build();
    }
}
