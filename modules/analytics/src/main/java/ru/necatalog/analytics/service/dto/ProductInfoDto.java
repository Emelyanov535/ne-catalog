package ru.necatalog.analytics.service.dto;

import lombok.Data;
import ru.necatalog.persistence.enumeration.Marketplace;

import java.math.BigDecimal;

@Data
public class ProductInfoDto {

	private String url;

	private Marketplace marketplace;

	private String productName;

	private String imageUrl;

	private Double percentChange;

	private BigDecimal price;
}
