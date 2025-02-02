package ru.ulstu.parsingservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.ulstu.parsingservice.config.properties.OzonConfigProperties;
import ru.ulstu.parsingservice.config.properties.WildberriesConfigProperties;

@Getter
@Configuration
@EnableConfigurationProperties({
    OzonConfigProperties.class,
    WildberriesConfigProperties.class
})
@AllArgsConstructor
public class MarketplacesConfig {
    private final WildberriesConfigProperties wildberriesConfigProperties;
    private final OzonConfigProperties ozonConfigProperties;
}
