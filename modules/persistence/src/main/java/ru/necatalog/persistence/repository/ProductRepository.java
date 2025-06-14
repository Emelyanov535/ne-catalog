package ru.necatalog.persistence.repository;

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

import java.util.List;
import java.util.Optional;

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

	@Query("""
			select p.category from ProductEntity p where p.url = :productUrl
			""")
	Category getProductCategory(@Param("productUrl") String productUrl);

	@Query(value = """
			        SELECT p.*
			        FROM product p
			        LEFT JOIN product_attribute pa ON p.url = pa.product_url
			        WHERE pa.product_url IS NULL
			""", nativeQuery = true)
	List<ProductEntity> getProductsWithoutAttributes();

	@Query("""
        select p from ProductEntity p where p.url not in (select distinct pa.id.productUrl from ProductAttributeEntity pa)
                and p.marketplace = "OZON"
                """)
	List<ProductEntity> findWithoutCharacteristics();

}