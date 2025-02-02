package ru.ulstu.parsingservice.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.ulstu.parsingservice.enumeration.Category;
import ru.ulstu.parsingservice.enumeration.Marketplace;

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
