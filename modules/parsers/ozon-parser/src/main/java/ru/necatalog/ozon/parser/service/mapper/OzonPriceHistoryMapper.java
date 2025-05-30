package ru.necatalog.ozon.parser.service.mapper;

import java.util.List;

import ru.necatalog.ozon.parser.service.dto.PriceHistoryDto;
import ru.necatalog.persistence.entity.PriceHistoryEntity;

public class OzonPriceHistoryMapper {

    public PriceHistoryDto toPriceHistoryDto (List<PriceHistoryEntity> priceHistory) {
        var priceHistoryDto = new PriceHistoryDto();
        priceHistory.forEach(item ->
            priceHistoryDto.getPriceHistory().put(item.getId().getDate().withNano(0), item.getPrice()));
        return priceHistoryDto;
    }

}
