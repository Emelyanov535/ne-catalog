package ru.necatalog.analytics.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class StatsData {
	private LocalDate date;
	private MarketplaceData wildberries;
	private MarketplaceData ozon;
	private MarketplaceData general;

	@Data
	@Builder
	public static class MarketplaceData {
		private BigDecimal min;
		private BigDecimal max;
		private BigDecimal avg;
	}
}
