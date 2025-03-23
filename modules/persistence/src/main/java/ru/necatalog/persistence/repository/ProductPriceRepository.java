package ru.necatalog.persistence.repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.id.PriceHistoryId;
import ru.necatalog.persistence.repository.projection.PriceValueData;

@Repository
public interface ProductPriceRepository extends JpaRepository<PriceHistoryEntity, PriceHistoryId> {

    List<PriceHistoryEntity> findAllById_ProductUrlAndIdDateAfterAndId_DateBeforeOrderById_DateAsc(String productUrl,
                                                                                                   ZonedDateTime from,
                                                                                                   ZonedDateTime to);

    @Query(value = """
            select price
            from price_history ph
            where product_url = :productUrl
            order by ph."date" desc limit 1
    """, nativeQuery = true)
    BigDecimal findLatestPriceByProductUrl(@Param("productUrl") String productUrl);


//     Правильный метод для сбора средних цен на товар за последние 20 дней для прогноза
//    @Query(value = """
//            select
//                date_trunc('day', ph.date) as day,
//                avg(ph.price) as average_price
//            from price_history ph
//            where ph.product_url = :productUrl
//              and ph.date >= current_date - interval '20' day
//            group by day
//            order by day desc
//            limit 20
//    """, nativeQuery = true)
//    List<PriceValueData> getPriceValueDataByProductUrl(@Param("productUrl") String productUrl);

    @Query(value = """
            select
                ph.date as date,
                ph.price as price
            from price_history ph
            where ph.product_url = :productUrl
            order by date desc
    """, nativeQuery = true)
    List<PriceValueData> getPriceValueDataByProductUrl(@Param("productUrl") String productUrl);

}
