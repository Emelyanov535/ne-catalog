package ru.necatalog.analytics.web.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.analytics.service.ForecastService;
import ru.necatalog.analytics.service.ProductFinderService;
import ru.necatalog.analytics.web.dto.ForecastWithHistoricalAndPredData;
import ru.necatalog.persistence.entity.ProductEntity;

@RestController
@RequestMapping("/forecast")
@RequiredArgsConstructor
@Tag(name = "Прогноз", description = "Прогнозирование цен с помощью временных рядов")
public class ForecastController {
	private final ForecastService forecastService;
	private final ProductFinderService productFinderService;

	@GetMapping
	@Operation(summary = "Получить прогноз временного ряда")
	public ResponseEntity<ForecastWithHistoricalAndPredData> getForecast(@RequestParam("url") String url) {
		return ResponseEntity.ok(forecastService.getForecast(url));
	}

	@GetMapping("/test")
	public ResponseEntity<List<ProductEntity>> test(@RequestParam("search") String search) {
		return ResponseEntity.ok(productFinderService.findSimilarProducts(search));
	}
}
