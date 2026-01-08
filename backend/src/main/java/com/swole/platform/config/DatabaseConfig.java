package com.swole.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.swole.platform.repository")
public class DatabaseConfig {
    // JPA configuration is handled through application.yml
}