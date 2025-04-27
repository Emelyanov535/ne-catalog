package ru.necatalog.app.config;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import ru.necatalog.auth.configuration.properties.JwtConfigProperties;
import ru.necatalog.notifications.configuration.properties.MailConfigProperties;

@Configuration
@EnableConfigurationProperties({
        MailConfigProperties.class,
		JwtConfigProperties.class
})
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class AppConfig {
}
