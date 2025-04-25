package ru.necatalog.analogfinder.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.necatalog.persistence.enumeration.Marketplace;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalogResult {

    private String productName;
    private String url;
    private String brand;
    private Marketplace marketplace;
    private String imageUrl;

}
