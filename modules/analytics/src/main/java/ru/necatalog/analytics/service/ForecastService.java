package ru.necatalog.analytics.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.necatalog.analytics.configuration.RestConfig;
import ru.necatalog.analytics.service.dto.ForecastRequest;
import ru.necatalog.analytics.service.dto.ForecastResponse;
import ru.necatalog.analytics.service.dto.ValueDto;
import ru.necatalog.analytics.web.dto.ForecastWithHistoricalAndPredData;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.projection.PriceStatsData;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ForecastService {

	private final RestTemplate restTemplateForecast;
	private final RestConfig restConfig;
	private final ProductPriceRepository productPriceRepository;

	public ForecastService(
			@Qualifier("restTemplateForecast") RestTemplate restTemplateForecast,
			RestConfig restConfig,
			ProductPriceRepository productPriceRepository) {
		this.restTemplateForecast = restTemplateForecast;
		this.restConfig = restConfig;
		this.productPriceRepository = productPriceRepository;
	}

	public String getForecast(String productUrl) {
		List<PriceStatsData> dailyAverages = productPriceRepository.getDailyPriceStatsByProductUrls(List.of(productUrl));

		List<PriceStatsData> filtered = dailyAverages.stream()
				.filter(d -> d.getAvgPrice() != null)
				.toList();

		if (filtered.size() <= 2) {
			throw new IllegalArgumentException("Недостаточно данных для прогноза");
		}

		ForecastRequest forecastRequest = createForecastRequest(filtered);

		ForecastResponse forecastResponse = restTemplateForecast.postForObject(
				restConfig.getForecastService().getMethods().getSpecificMethodForecast(),
				new HttpEntity<>(forecastRequest),
				ForecastResponse.class
		);

		ForecastWithHistoricalAndPredData q = ForecastWithHistoricalAndPredData.builder()
				.historical(filtered.stream()
						.map(d -> ValueDto.builder()
								.date(d.getDate().toString())
								.value(d.getAvgPrice())
								.build())
						.toList())
				.prediction(forecastResponse.getTimeSeries().getValues().stream().skip(1).toList())
				.build();

		return getPurchaseRecommendation(q.getHistorical(), q.getPrediction());
	}

	private String getPurchaseRecommendation(List<ValueDto> historicalValues, List<ValueDto> predictionValues) {
		BigDecimal lastHistoricalValue = historicalValues.get(historicalValues.size() - 1).getValue();

		BigDecimal firstPredictedValue = predictionValues.get(0).getValue();

		if (firstPredictedValue.compareTo(lastHistoricalValue) < 0) {
			return "Не торопитесь с покупкой, цена возможно упадёт.";
		} else {
			return "Советуем купить сейчас, возможно цена в ближайшее время пойдет наверх.";
		}
	}

	private ForecastRequest createForecastRequest(List<PriceStatsData> dailyData) {
		List<ValueDto> values = dailyData.stream()
				.map(d -> ValueDto.builder()
						.date(d.getDate().toString() + "T00:00:00")
						.value(d.getAvgPrice())
						.build())
				.toList();

		return ForecastRequest.builder()
				.originalTimeSeries(ForecastRequest.OriginalTimeSeries.builder()
						.length(values.size())
						.values(values)
						.build())
				.methodClassName("AddTrendAddSeason")
				.countForecast(values.size() / 4)
				.build();
	}
}
