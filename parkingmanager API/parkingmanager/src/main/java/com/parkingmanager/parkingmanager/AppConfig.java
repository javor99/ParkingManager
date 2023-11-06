package com.parkingmanager.parkingmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AppConfig {
    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        // Create a single-threaded executor service
        return Executors.newSingleThreadScheduledExecutor();
    }

}
