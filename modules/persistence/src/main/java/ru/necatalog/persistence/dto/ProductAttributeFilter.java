package ru.necatalog.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeFilter {

    private Long attributeId;

    private String value;

    private String unit;

}
