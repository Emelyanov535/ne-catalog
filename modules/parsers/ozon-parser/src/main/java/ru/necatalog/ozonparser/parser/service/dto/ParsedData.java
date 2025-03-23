package ru.necatalog.ozonparser.parser.service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.necatalog.ozonparser.parser.enumeration.OzonCategory;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;

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

    public String getCategoryUrl() {
        return OzonCategory.valueOf(this.category.name()).getCategoryUrl();
    }

}
