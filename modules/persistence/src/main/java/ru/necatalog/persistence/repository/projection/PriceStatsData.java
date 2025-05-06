package ru.necatalog.persistence.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PriceStatsData {
	LocalDate getDate();
	BigDecimal getMinPrice();
	BigDecimal getMaxPrice();
	BigDecimal getAvgPrice();
}
