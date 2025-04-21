package ru.necatalog.persistence.repository.projection;

import ru.necatalog.persistence.enumeration.Marketplace;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SimilarProductData {
	String getUrl();

	Marketplace getMarketplace();

	String getProductName();

	String getImageUrl();

	Double getPercentChange();

	BigDecimal getPrice();
}
