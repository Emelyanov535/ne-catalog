package ru.necatalog.analytics.web.dto;

import lombok.Builder;
import lombok.Data;
import ru.necatalog.persistence.repository.projection.PriceStatsData;

import java.util.List;

@Data
@Builder
public class AnalysisDataDto {
	private List<PriceStatsData> priceStats;
	private String bestPurchaseTime;
	private String predictionAdvice;
}
