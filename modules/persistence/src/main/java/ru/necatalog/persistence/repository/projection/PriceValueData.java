package ru.necatalog.persistence.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PriceValueData {
	LocalDateTime getDate();

	BigDecimal getPrice();
}
