package ru.necatalog.wildberriesparser.wildberries_parser.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInfoDto {
    private Long id;
    private String brand;
    private String name;
    private String supplier;
    private Double supplierRating;
    private Integer salePriceU;
    private Integer reviewRating;
}
