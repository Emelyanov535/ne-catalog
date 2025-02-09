package ru.necatalog.wildberriesparser.service.mapper;

import org.springframework.stereotype.Component;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.wildberriesparser.service.dto.ProductDto;

@Component
public class ProductMapper {

    public ProductDto toProductDto(ProductEntity product) {
        return new ProductDto(
            product.getId(),
            product.getMarketplace(),
            product.getCategory(),
            product.getBrand(),
            product.getProductName(),
            product.getUrl(),
            product.getImageUrl()
        );
    }

}
