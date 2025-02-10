package ru.necatalog.persistence.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.PriceHistoryId;

@Repository
public interface ProductPriceRepository extends JpaRepository<PriceHistoryEntity, PriceHistoryId> {

    List<PriceHistoryEntity> findAllById_ProductUrlAndIdDateAfterAndId_DateBeforeOrderById_DateAsc(String productUrl,
                                                                                                   ZonedDateTime from,
                                                                                                   ZonedDateTime to);

}
