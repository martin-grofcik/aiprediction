package org.crp.aiflow;

import org.crp.aiflow.services.AiPredictionTaskService;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(proxyBeanMethods = false)
public class AiFlowApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AiFlowApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AiFlowApplication.class);
    }

    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> replaceTaskService(AiPredictionTaskService aiPredictionTaskService) {
        return configuration -> configuration.setTaskService(aiPredictionTaskService);
    }
}