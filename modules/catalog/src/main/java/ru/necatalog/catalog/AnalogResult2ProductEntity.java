package ru.necatalog.catalog;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.necatalog.analogfinder.dto.AnalogResult;
import ru.necatalog.persistence.entity.ProductEntity;

@Component
public class AnalogResult2ProductEntity implements Converter<AnalogResult, ProductEntity> {
	@Override
	public ProductEntity convert(AnalogResult source) {
		return ProductEntity.builder()
				.productName(source.getProductName())
				.url(source.getUrl())
				.brand(source.getBrand())
				.marketplace(source.getMarketplace())
				.imageUrl(source.getImageUrl())
				.build();
	}
}
