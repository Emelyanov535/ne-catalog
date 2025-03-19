package ru.necatalog.analytics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.necatalog.analytics.configuration.RestConfig;
import ru.necatalog.analytics.service.dto.ForecastRequest;
import ru.necatalog.analytics.service.dto.ForecastResponse;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.projection.PriceValueData;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForecastService {
	private final RestTemplate restTemplateForecast;
	private final RestConfig restConfig;
	private final ProductPriceRepository productPriceRepository;

	public ForecastResponse getForecast(String productUrl) {
		List<PriceValueData> priceValueData = productPriceRepository.getPriceValueDataByProductUrl(productUrl);

		ForecastRequest forecastRequest = createForecastRequest(priceValueData);

		return restTemplateForecast.postForObject(
				restConfig.getForecastService().getMethods().getForecast(),
				new HttpEntity<>(forecastRequest),
				ForecastResponse.class
		);
	}

	private ForecastRequest createForecastRequest(List<PriceValueData> priceValueData) {
		List<ForecastRequest.Value> values = priceValueData.stream()
				.map(pv -> ForecastRequest.Value.builder()
						.date(pv.getDate().toString())
						.value(pv.getPrice())
						.build())
				.toList();


		ForecastRequest.OriginalTimeSeries originalTimeSeries = ForecastRequest.OriginalTimeSeries.builder()
				.values(values)
				.build();

		return ForecastRequest.builder()
				.originalTimeSeries(originalTimeSeries)
				.methodClassName("FTransform")
				.countForecast(Math.max(5, values.size() / 4))
				.build();
	}
}
