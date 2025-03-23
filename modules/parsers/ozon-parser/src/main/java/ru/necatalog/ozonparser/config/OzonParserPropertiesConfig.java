package ru.necatalog.ozonparser.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.necatalog.config.YamlPropertySourceFactory;
import ru.necatalog.ozonparser.config.properties.OzonParserProperties;

@Slf4j
@Configuration
@EnableConfigurationProperties(OzonParserProperties.class)
@PropertySource(value = "classpath:application-ozon-parser.yml", factory = YamlPropertySourceFactory.class)
public class OzonParserPropertiesConfig {

    @PostConstruct
    public void logProperty() {
        log.info("Ozon Parser Enabled");
    }

}
