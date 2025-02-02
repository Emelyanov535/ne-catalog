package ru.ulstu.parsingservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "marketplace.ozon")
public class OzonConfigProperties {

    private Integer maxThreads;

    private Integer maxNumOfPagesOnScreen;

}
