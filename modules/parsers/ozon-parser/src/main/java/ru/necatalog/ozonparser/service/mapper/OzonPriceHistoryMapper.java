package ru.necatalog.ozonparser.service.mapper;

import java.util.List;

import ru.necatalog.ozonparser.service.dto.PriceHistoryDto;
import ru.necatalog.persistence.entity.PriceHistoryEntity;

public class OzonPriceHistoryMapper {

    public PriceHistoryDto toPriceHistoryDto (List<PriceHistoryEntity> priceHistory) {
        var priceHistoryDto = new PriceHistoryDto();
        priceHistory.forEach(item ->
            priceHistoryDto.getPriceHistory().put(item.getId().getDate().withNano(0), item.getPrice()));
        return priceHistoryDto;
    }

}
