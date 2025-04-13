package ru.necatalog.analytics.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.necatalog.analytics.configuration.RestConfig;
import ru.necatalog.analytics.service.dto.ForecastRequest;
import ru.necatalog.analytics.service.dto.ForecastResponse;
import ru.necatalog.analytics.service.dto.ValueDto;
import ru.necatalog.analytics.web.dto.ForecastWithHistoricalAndPredData;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.projection.PriceValueData;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForecastService {
	private final RestTemplate restTemplateForecast;
	private final RestConfig restConfig;
	private final ProductPriceRepository productPriceRepository;

	@SneakyThrows
	public ForecastWithHistoricalAndPredData getForecast(String productUrl) {
		List<PriceValueData> priceValueData = productPriceRepository.getPriceValueDataByProductUrl(productUrl);

		if (priceValueData.size() <= 2) {
			throw new RuntimeException();
		}

		ForecastRequest forecastRequest = createForecastRequest(priceValueData);

		ForecastResponse forecastResponse = restTemplateForecast.postForObject(
				restConfig.getForecastService().getMethods().getSpecificMethodForecast(),
				new HttpEntity<>(forecastRequest),
				ForecastResponse.class
		);

		return ForecastWithHistoricalAndPredData.builder()
				.historical(priceValueData.stream()
						.map(pv -> ValueDto.builder()
								.date(pv.getDate().toString())
								.value(pv.getPrice())
								.build())
						.toList())
				.prediction(forecastResponse.getTimeSeries().getValues().stream().skip(1).toList())
				.build();
	}

	private ForecastRequest createForecastRequest(List<PriceValueData> priceValueData) {
		List<ValueDto> values = priceValueData.stream()
				.map(pv -> ValueDto.builder()
						.date(pv.getDate().toString())
						.value(pv.getPrice())
						.build())
				.toList();

		ForecastRequest.OriginalTimeSeries originalTimeSeries = ForecastRequest.OriginalTimeSeries.builder()
				.length(values.size())
				.values(values)
				.build();

		return ForecastRequest.builder()
				.originalTimeSeries(originalTimeSeries)
				.methodClassName("AddTrendAddSeason")
				.countForecast(values.size() / 4)
				.build();
	}
}
