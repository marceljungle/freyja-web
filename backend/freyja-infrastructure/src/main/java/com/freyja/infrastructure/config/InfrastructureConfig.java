package com.freyja.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, MqttProperties.class, OpenCellIdProperties.class})
public class InfrastructureConfig {

}
