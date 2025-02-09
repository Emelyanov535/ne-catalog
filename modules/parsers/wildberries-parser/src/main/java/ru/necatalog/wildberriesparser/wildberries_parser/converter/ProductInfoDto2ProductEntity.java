package ru.necatalog.wildberriesparser.wildberries_parser.converter;

import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;
import ru.necatalog.wildberriesparser.wildberries_parser.service.dto.ProductInfoDto;

@Component
public class ProductInfoDto2ProductEntity implements Converter<ProductInfoDto, ProductEntity> {
    @Override
    public ProductEntity convert(ProductInfoDto source) {
        return ProductEntity.builder()
                .marketplace(Marketplace.WILDBERRIES)
                .category(Category.LAPTOP)
                .brand(source.getBrand())
                .productName(source.getName())
                .createdAt(LocalDateTime.now())
                .imageUrl("")
                .build();
    }
}
