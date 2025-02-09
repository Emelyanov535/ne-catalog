package ru.necatalog.ozonparser.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductDto {

    private final Long id;

    private final Marketplace marketplace;

    private final Category category;

    private final String brand;

    private final String productName;

    private final String url;

    private final String imageUrl;

}
