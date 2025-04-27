package ru.necatalog.wildberriesparser.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

	@Bean
	@Primary
	public RestTemplate restTemplateScrapping() {
		return new RestTemplateBuilder()
				.build();
	}
}
