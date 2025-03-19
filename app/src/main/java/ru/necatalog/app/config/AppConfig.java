package ru.necatalog.app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.necatalog.auth.configuration.properties.JwtConfigProperties;
import ru.necatalog.notifications.configuration.properties.MailConfigProperties;

@Configuration
@EnableConfigurationProperties({
        MailConfigProperties.class,
		JwtConfigProperties.class
})
public class AppConfig {
}
