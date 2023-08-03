package com.itrailmpool.itrailmpoolviewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ItrailmpoolViewerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItrailmpoolViewerApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(ItrailmpoolViewerApplication.class, args);

            LOGGER.debug("Itrailmpool Viewer started");
        } catch (Exception e) {
            LOGGER.error("Unhandled exception", e);
        }
    }
}
