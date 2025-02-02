package ru.ulstu.parsingservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.ulstu.parsingservice.persistence.entity.ProductEntity;
import ru.ulstu.parsingservice.service.dto.ProductDto;

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
