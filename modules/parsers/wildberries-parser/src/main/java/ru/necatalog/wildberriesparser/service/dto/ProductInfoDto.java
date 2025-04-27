package ru.necatalog.wildberriesparser.service.dto;

import java.util.List;

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
    private Integer reviewRating;
    private List<Sizes> sizes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Sizes {
        private Price price;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class Price {
            private Integer basic;
            private Integer product;
            private Integer total;
        }
    }
}
