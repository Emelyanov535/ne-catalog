package ru.necatalog.analytics.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
	private final RestConfig restConfig;

	@Bean
	public RestTemplate restTemplateForecast() {
		return new RestTemplateBuilder()
				.rootUri(restConfig.getForecastService().getHost())
				.build();
	}
}
