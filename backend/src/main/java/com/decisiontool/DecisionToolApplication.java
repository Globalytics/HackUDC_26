package com.decisiontool;

import com.decisiontool.config.DenodoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DenodoConfig.class)
public class DecisionToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DecisionToolApplication.class, args);
    }
}