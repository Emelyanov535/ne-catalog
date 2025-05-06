package ru.necatalog.analytics.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.analytics.service.AnalysisService;
import ru.necatalog.analytics.service.ForecastService;
import ru.necatalog.analytics.service.PriceAnalyzerService;
import ru.necatalog.analytics.web.dto.AnalysisDataDto;
import ru.necatalog.analytics.web.dto.StatsData;

import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Tag(name = "Анализ", description = "Анализ цены по истории цен")
public class AnalysisController {

	private final AnalysisService analysisService;
	private final PriceAnalyzerService priceAnalyzerService;
	private final ForecastService forecastService;

	@GetMapping
	@Operation(summary = "Получить анализ товара")
	public ResponseEntity<AnalysisDataDto> getHistoricalGraphics(@RequestParam("url") String url) {

		AnalysisDataDto analysisDataDto = AnalysisDataDto.builder()
				.priceStats(analysisService.getHistoricalValue(url))
				.bestPurchaseTime(priceAnalyzerService.analyzeBestPurchaseTime(url))
				.predictionAdvice(forecastService.getForecast(url))
				.build();

		return ResponseEntity.ok(analysisDataDto);
	}

	@GetMapping("/identProdStats")
	@Operation(summary = "Получить график цен идентичных товаров")
	public ResponseEntity<List<StatsData>> getHistoricalPricesForIdenticalProducts(@RequestParam("url") String url) {
		return ResponseEntity.ok(analysisService.getHistoricalPricesForIdenticalProducts(url));
	}
}
