package ru.necatalog.analytics.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.id.PriceHistoryId;
import ru.necatalog.persistence.repository.ProductPriceRepository;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PriceAnalyzerServiceTest {

	private ProductPriceRepository priceHistoryRepository;
	private PriceAnalyzerService analyzerService;

	@BeforeEach
	void setUp() {
		priceHistoryRepository = mock(ProductPriceRepository.class);
		analyzerService = new PriceAnalyzerService(priceHistoryRepository);
	}

	@Test
	void testAnalyzeBestPurchaseTime_returnsBestTimeMessage() {
		String productUrl = "test-url";

		ZonedDateTime mondayMorning = ZonedDateTime.of(2024, 5, 6, 8, 0, 0, 0, ZoneId.of("Europe/Moscow")); // понедельник, утро
		ZonedDateTime mondayEvening = ZonedDateTime.of(2024, 5, 6, 20, 0, 0, 0, ZoneId.of("Europe/Moscow")); // понедельник, вечер
		ZonedDateTime saturdayMorning = ZonedDateTime.of(2024, 5, 11, 9, 0, 0, 0, ZoneId.of("Europe/Moscow")); // выходной, утро

		List<PriceHistoryEntity> mockData = List.of(
				priceEntity(mondayMorning, new BigDecimal("800")),
				priceEntity(mondayEvening, new BigDecimal("1200")),
				priceEntity(saturdayMorning, new BigDecimal("900"))
		);

		when(priceHistoryRepository.findAllByIdProductUrlAndIdDateAfter(eq(productUrl), any()))
				.thenReturn(mockData);

		String result = analyzerService.analyzeBestPurchaseTime(productUrl);

		assertTrue(result.contains("утро"));
		assertTrue(result.contains("будни"));
		assertTrue(result.contains("₽"));
	}

	@Test
	void testAnalyzeBestPurchaseTime_returnsNotEnoughDataMessage() {
		String productUrl = "test-url";

		when(priceHistoryRepository.findAllByIdProductUrlAndIdDateAfter(eq(productUrl), any()))
				.thenReturn(List.of());

		String result = analyzerService.analyzeBestPurchaseTime(productUrl);

		assertEquals("Недостаточно данных за последние 30 дней.", result);
	}

	private PriceHistoryEntity priceEntity(ZonedDateTime dateTime, BigDecimal price) {
		PriceHistoryEntity entity = new PriceHistoryEntity();
		PriceHistoryId id = new PriceHistoryId();
		id.setProductUrl("test-url");
		id.setDate(dateTime);
		entity.setId(id);
		entity.setPrice(price);
		return entity;
	}
}
