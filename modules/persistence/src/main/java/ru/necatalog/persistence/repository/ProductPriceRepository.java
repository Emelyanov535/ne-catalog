package ru.necatalog.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.id.PriceHistoryId;
import ru.necatalog.persistence.repository.projection.PriceStatsData;
import ru.necatalog.persistence.repository.projection.PriceValueData;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ProductPriceRepository extends JpaRepository<PriceHistoryEntity, PriceHistoryId> {

	List<PriceHistoryEntity> findAllById_ProductUrlAndIdDateAfterAndId_DateBeforeOrderById_DateAsc(String productUrl,
																								   ZonedDateTime from,
																								   ZonedDateTime to);

	@Query(value = """
			    select ph
			    from PriceHistoryEntity ph
			    where ph.id.productUrl = :productUrl
			    order by ph.id.date desc
				limit 1 offset 1
			""")
	PriceHistoryEntity findLatestPriceByProductUrl(@Param("productUrl") String productUrl);


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
			        order by date
			""", nativeQuery = true)
	List<PriceValueData> getPriceValueDataByProductUrl(@Param("productUrl") String productUrl);

	@Query(value = """
			        select
			            last_price			
			        from product where url = :productUrl
			""", nativeQuery = true)
	Long getPrice(@Param("productUrl") String productUrl);


	@Query(value = """
			WITH last_14_days AS (
			    SELECT generate_series(CURRENT_DATE - INTERVAL '13 days', CURRENT_DATE, INTERVAL '1 day')::date AS date
			)
			SELECT
			    d.date AS date,
			    MIN(ph.price) AS min_price,
			    MAX(ph.price) AS max_price,
			    ROUND(AVG(ph.price), 2) AS avg_price
			FROM last_14_days d
			LEFT JOIN price_history ph
			    ON DATE(ph.date) = d.date AND ph.product_url IN (:productUrls)
			GROUP BY d.date
			ORDER BY d.date
			""", nativeQuery = true)
	List<PriceStatsData> getDailyPriceStatsByProductUrls(@Param("productUrls") List<String> productUrls);


	List<PriceHistoryEntity> findAllByIdProductUrlAndIdDateAfter(String productUrl, ZonedDateTime date);

}
