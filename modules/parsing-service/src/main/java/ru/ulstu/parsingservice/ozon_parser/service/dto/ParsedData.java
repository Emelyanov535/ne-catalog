package ru.ulstu.parsingservice.ozon_parser.service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.ulstu.parsingservice.enumeration.Category;
import ru.ulstu.parsingservice.enumeration.Marketplace;

@Getter
@Setter
@Builder
public class ParsedData {

    private Marketplace marketplace;

    private Category category;

    private String brand;

    private String productName;

    private String url;

    private String imageUrl;

    private BigDecimal price;

}
