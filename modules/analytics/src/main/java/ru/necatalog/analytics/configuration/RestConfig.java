package ru.necatalog.analytics.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest")
@Getter
@Setter
public class RestConfig {

	private ForecastService forecastService;

 	@Getter
	@Setter
 	public static class ForecastService {
		 private String host;
		 private Methods methods;

		@Getter
		@Setter
		public static class Methods {
			private String specificMethodForecast;
			private String specificMethodSmoothed;
			private String forecast;
		}
	}
}
