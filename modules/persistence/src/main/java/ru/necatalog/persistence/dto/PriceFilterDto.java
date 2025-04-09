package ru.necatalog.persistence.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceFilterDto {

    BigDecimal priceStart;

    BigDecimal priceEnd;

}
