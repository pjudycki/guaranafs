package com.guarana.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "exchange.rate.api")
@ConfigurationPropertiesScan
@Data
public class GuaranaConfigurationProperties {

    private String endpoint;
    private String key;

}