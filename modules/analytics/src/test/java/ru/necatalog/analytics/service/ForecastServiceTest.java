package ru.necatalog.analytics.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.necatalog.analytics.configuration.RestConfig;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.projection.PriceStatsData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForecastServiceTest {

	private static WireMockServer wireMockServer;

	private ForecastService forecastService;

	@BeforeAll
	static void startWireMock() {
		wireMockServer = new WireMockServer(8888);
		wireMockServer.start();
	}

	@AfterAll
	static void stopWireMock() {
		wireMockServer.stop();
	}

	@BeforeEach
	void setUp() {
		// Mock repo
		ProductPriceRepository mockRepo = mock(ProductPriceRepository.class);

		List<PriceStatsData> mockStats = List.of(
				priceData(LocalDate.of(2024, 1, 1), new BigDecimal("100")),
				priceData(LocalDate.of(2024, 1, 2), new BigDecimal("105")),
				priceData(LocalDate.of(2024, 1, 3), new BigDecimal("110")),
				priceData(LocalDate.of(2024, 1, 4), new BigDecimal("115"))
		);

		when(mockRepo.getDailyPriceStatsByProductUrls(List.of("test-url")))
				.thenReturn(mockStats);

		// RestTemplate
		RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());

		// RestConfig
		RestConfig config = new RestConfig();
		RestConfig.ForecastService.Methods method = new RestConfig.ForecastService.Methods();
		method.setSpecificMethodForecast("http://localhost:8888/forecast");
		RestConfig.ForecastService serviceConfig = new RestConfig.ForecastService();
		serviceConfig.setMethods(method);
		config.setForecastService(serviceConfig);

		forecastService = new ForecastService(restTemplate, config, mockRepo);
	}

	@Test
	void testGetForecast_ReturnsPurchaseAdvice() {
		String jsonResponse = """
            {
              "timeSeries": {
                "values": [
                  {"date": "2024-01-05T00:00:00", "value": 113},
                  {"date": "2024-01-06T00:00:00", "value": 117}
                ]
              }
            }
        """;

		wireMockServer.stubFor(post(urlEqualTo("/forecast"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody(jsonResponse)));

		String recommendation = forecastService.getForecast("test-url");

		assertTrue(recommendation.contains("купить сейчас") || recommendation.contains("Не торопитесь"));
	}

	private PriceStatsData priceData(LocalDate date, BigDecimal avg) {
		return new PriceStatsData() {
			@Override public LocalDate getDate() { return date; }
			@Override public BigDecimal getAvgPrice() { return avg; }
			@Override public BigDecimal getMinPrice() { return avg; }
			@Override public BigDecimal getMaxPrice() { return avg; }
		};
	}
}
