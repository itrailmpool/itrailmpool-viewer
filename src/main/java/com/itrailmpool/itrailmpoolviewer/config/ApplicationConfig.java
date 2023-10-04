package com.itrailmpool.itrailmpoolviewer.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource(value = "classpath:${env:dev}/application.properties")
public class ApplicationConfig {

    public static final String DEFAULT_DATA_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String LOCAL_DATA_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String UTC_TIMEZONE = "UTC";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public RestTemplate restTemplate() {
        if (LOGGER.isDebugEnabled()) {
            ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
            RestTemplate restTemplate = new RestTemplate(factory);
            restTemplate.getInterceptors().add(new LoggingInterceptor());

            return restTemplate;
        }

        return new RestTemplate();
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

    public static class LoggingInterceptor implements ClientHttpRequestInterceptor {

        private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] requestBody, ClientHttpRequestExecution execution) throws IOException {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Request body: {}", new String(requestBody, StandardCharsets.UTF_8));
            }

            ClientHttpResponse response = execution.execute(request, requestBody);

            if (LOGGER.isDebugEnabled()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
                String responseBody = bufferedReader.lines().collect(Collectors.joining("\n"));
                LOGGER.debug("Response body: {}", responseBody);
            }

            return response;
        }
    }

}
