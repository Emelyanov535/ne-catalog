package ru.ulstu.parsingservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.ulstu.parsingservice.config.properties.SeleniumConfigProperties;

@Configuration
@EnableConfigurationProperties(SeleniumConfigProperties.class)
public class SeleniumConfig {
}
