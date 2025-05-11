package ru.necatalog.ozon.parser.parsing.dto.catalog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OzonCatalogPageProductData {

    private String brand;

    private String productName;

    private String url;

    private String imageUrl;

    private String price;

}