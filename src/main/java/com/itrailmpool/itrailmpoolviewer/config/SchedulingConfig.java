package com.itrailmpool.itrailmpoolviewer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@PropertySource(value = "classpath:${env:dev}/application.properties")
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Value("${app.scheduling.enable}")
    private Boolean enable;

    public Boolean isEnable() {
        return enable;
    }
}
