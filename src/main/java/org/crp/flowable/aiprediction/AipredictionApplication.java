package org.crp.flowable.aiprediction;

import org.crp.flowable.aiprediction.services.AiPredictionTaskService;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(proxyBeanMethods = false)
public class AipredictionApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AipredictionApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AipredictionApplication.class);
    }

    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> replaceTaskService(AiPredictionTaskService aiPredictionTaskService) {
        return configuration -> configuration.setTaskService(aiPredictionTaskService);
    }
}