package ru.necatalog.ozonparser.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ozon-parser")
public class OzonParserProperties {

    private boolean enabled;

    private int maxThreads;

    private int maxNumOfPagesOnScreen;

    private String mode;

}
