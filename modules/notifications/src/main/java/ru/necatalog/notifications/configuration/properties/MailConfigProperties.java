package ru.necatalog.notifications.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.mail")
public class MailConfigProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
