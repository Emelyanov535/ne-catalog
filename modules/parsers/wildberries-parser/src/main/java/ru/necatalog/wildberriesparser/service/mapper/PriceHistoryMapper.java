package ru.necatalog.wildberriesparser.service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.wildberriesparser.service.dto.PriceHistoryDto;

@Component
public class PriceHistoryMapper {

    public PriceHistoryDto toPriceHistoryDto (List<PriceHistoryEntity> priceHistory) {
        var priceHistoryDto = new PriceHistoryDto();
        priceHistory.forEach(item ->
            priceHistoryDto.getPriceHistory().put(item.getId().getDate().withNano(0), item.getPrice()));
        return priceHistoryDto;
    }

}
