package ru.ulstu.parsingservice.persistence.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ulstu.parsingservice.persistence.entity.PriceHistoryEntity;
import ru.ulstu.parsingservice.persistence.entity.PriceHistoryId;

public interface ProductPriceRepository extends JpaRepository<PriceHistoryEntity, PriceHistoryId> {

    List<PriceHistoryEntity> findAllById_ProductUrlAndIdDateAfterAndId_DateBeforeOrderById_DateAsc(String productUrl,
                                                                                                   ZonedDateTime from,
                                                                                                   ZonedDateTime to);

}
