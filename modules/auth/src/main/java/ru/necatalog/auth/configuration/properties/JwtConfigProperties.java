package ru.necatalog.auth.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt.secret")
public class JwtConfigProperties {
	private String access;
	private String refresh;
}
