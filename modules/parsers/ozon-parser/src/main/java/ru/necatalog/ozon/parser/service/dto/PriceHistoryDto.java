package ru.necatalog.ozon.parser.service.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceHistoryDto {

    private final Map<ZonedDateTime, BigDecimal> priceHistory;

    public PriceHistoryDto() {
        this.priceHistory = new HashMap<>();
    }
}
