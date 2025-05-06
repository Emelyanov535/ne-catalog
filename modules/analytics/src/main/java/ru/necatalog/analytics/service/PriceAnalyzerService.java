package ru.necatalog.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.repository.ProductPriceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceAnalyzerService {

	private final ProductPriceRepository priceHistoryRepository;

	public String analyzeBestPurchaseTime(String productUrl) {
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime from = now.minusDays(30);

		List<PriceHistoryEntity> recent = priceHistoryRepository
				.findAllByIdProductUrlAndIdDateAfter(productUrl, from);

		if (recent.isEmpty()) {
			return "Недостаточно данных за последние 30 дней.";
		}

		// Группировка по "будни/выходные|утро/день/вечер/ночь"
		Map<String, List<PriceHistoryEntity>> grouped = recent.stream()
				.collect(Collectors.groupingBy(p -> {
					ZonedDateTime dateTime = p.getId().getDate();
					DayOfWeek day = dateTime.getDayOfWeek();
					boolean weekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
					String weekdayType = weekend ? "выходные" : "будни";

					int hour = dateTime.getHour();
					String timeSlot;
					if (hour >= 6 && hour < 12) timeSlot = "утро";
					else if (hour >= 12 && hour < 18) timeSlot = "день";
					else if (hour >= 18 && hour < 24) timeSlot = "вечер";
					else timeSlot = "ночь";

					return weekdayType + "|" + timeSlot;
				}));

		// Средняя цена по каждой группе
		Map<String, BigDecimal> avgPrices = new HashMap<>();
		for (Map.Entry<String, List<PriceHistoryEntity>> entry : grouped.entrySet()) {
			BigDecimal avg = entry.getValue().stream()
					.map(PriceHistoryEntity::getPrice)
					.reduce(BigDecimal.ZERO, BigDecimal::add)
					.divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP);
			avgPrices.put(entry.getKey(), avg);
		}

		// Поиск минимальной средней цены
		Optional<Map.Entry<String, BigDecimal>> bestGroup = avgPrices.entrySet().stream()
				.min(Map.Entry.comparingByValue());

		if (bestGroup.isPresent()) {
			String[] parts = bestGroup.get().getKey().split("\\|");
			return String.format("Лучшее время для покупки: %s %s — средняя цена: %s₽",
					parts[0], parts[1], bestGroup.get().getValue());
		} else {
			return "Невозможно определить оптимальное время для покупки.";
		}
	}
}
