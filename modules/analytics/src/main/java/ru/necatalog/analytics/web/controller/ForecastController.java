package ru.necatalog.analytics.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.analytics.service.ForecastService;
import ru.necatalog.analytics.service.dto.ForecastResponse;

@RestController
@RequestMapping("/forecast")
@RequiredArgsConstructor
@Tag(name = "Прогноз", description = "Прогнозирование цен с помощью временных рядов")
public class ForecastController {
	private final ForecastService forecastService;

	@PostMapping
	@Operation(summary = "Получить прогноз временного ряда методом FTransform")
	public ResponseEntity<ForecastResponse> login(@RequestBody String productUrl) {
		return ResponseEntity.ok(forecastService.getForecast(productUrl));
	}
}
