package ru.necatalog.ozonparser.service.mapper;

import ru.necatalog.ozonparser.service.dto.ProductDto;
import ru.necatalog.persistence.entity.ProductEntity;

public class OzonProductMapper {

    public ProductDto toProductDto(ProductEntity product) {
        return new ProductDto(
            product.getMarketplace(),
            product.getCategory(),
            product.getBrand(),
            product.getProductName(),
            product.getUrl(),
            product.getImageUrl()
        );
    }

}
