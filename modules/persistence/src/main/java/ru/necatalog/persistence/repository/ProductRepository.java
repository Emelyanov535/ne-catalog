package ru.necatalog.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.dto.PriceFilterDto;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;
import ru.necatalog.persistence.repository.projection.SimilarProductData;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {

    List<ProductEntity> findAllByUrlIn(List<String> urls);

    @Query("""
        select p.url from ProductEntity p where p.url in :urls
    """)
    List<String> findSavedUrl(List<String> urls);

    Optional<ProductEntity> findByUrl(String url);

    Page<ProductEntity> findAllByMarketplaceAndCategory(Marketplace marketplace, Category category, Pageable pageable);

    Page<ProductEntity> findAll(Pageable pageable);

    @Query(nativeQuery = true,
            value = "select max(ph.price) as priceEnd, min(ph.price) as priceStart from product p " +
            "join price_history ph on p.url = ph.product_url " +
            "where p.category = :category")
    PriceFilterDto findPriceFilterData(@Param("category") String category);

    @Query(value = """
        SELECT DISTINCT ON (p.product_name)
               p.marketplace,
               p.product_name,
               p.url,
               p.image_url,
               p.percent_change,
               ph.price
        FROM product p
        JOIN price_history ph ON p.url = ph.product_url
        WHERE similarity(p.product_name,
        	(select p.product_name
        	from product p
        	where p.url = :input)
        ) > 0.85
        ORDER BY p.product_name, ph.date DESC
    """, nativeQuery = true)
    List<SimilarProductData> findSimilarProducts(@Param("input") String input);

    @Query("""
        select p.category from ProductEntity p where p.url = :productUrl
        """)
    Category getProductCategory(@Param("productUrl") String productUrl);
}
