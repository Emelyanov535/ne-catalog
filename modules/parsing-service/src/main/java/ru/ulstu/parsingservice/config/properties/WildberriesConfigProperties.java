package ru.ulstu.parsingservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "marketplace.wildberries")
public class WildberriesConfigProperties {
    private String baseUrl;
    private String catalogUrl;
    private String userAgent;
    private String catalogWbUrl;
    private int retryAttempts;
    private long retryDelay;
    private String laptopUrl;
    private String shard;
}
