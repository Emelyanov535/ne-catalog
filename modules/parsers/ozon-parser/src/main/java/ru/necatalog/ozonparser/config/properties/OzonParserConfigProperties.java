package ru.necatalog.ozonparser.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ozon-parser")
public class OzonParserConfigProperties {

    private boolean enabled;

    private Integer maxThreads;

    private Integer maxNumOfPagesOnScreen;

}
