package ru.necatalog.analytics.service.dto;

import ru.necatalog.persistence.repository.projection.PriceValueData;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MockPriceValueData implements PriceValueData {
	private final LocalDateTime date;
	private final BigDecimal price;

	public MockPriceValueData(LocalDateTime date, BigDecimal price) {
		this.date = date;
		this.price = price;
	}

	@Override
	public LocalDateTime getDate() {
		return date;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}
}

