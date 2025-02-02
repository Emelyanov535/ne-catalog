package ru.ulstu.parsingservice.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductsPageDto {

    private final int totalItems;

    private final int totalPages;

    private final int currentPage;

    private final List<ProductDto> products;

}
