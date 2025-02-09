package ru.necatalog.wildberriesparser.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.necatalog.wildberriesparser.config.properties.WildberriesConfigProperties;

@Getter
@Configuration
@EnableConfigurationProperties(WildberriesConfigProperties.class)
@AllArgsConstructor
public class MarketplacesConfig {
}
