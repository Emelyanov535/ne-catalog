package ru.necatalog.search.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.necatalog.persistence.enumeration.Marketplace;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {

    private String productName;
    private String url;
    private String brand;
    private Marketplace marketplace;
    private String imageUrl;
    private BigDecimal price;
    private Double percentChange;

}
