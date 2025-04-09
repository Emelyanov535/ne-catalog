package ru.necatalog.search.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterData {

    private BigDecimal priceStart;

    private BigDecimal priceEnd;

    private Map<String, List<String>> filters;

}
