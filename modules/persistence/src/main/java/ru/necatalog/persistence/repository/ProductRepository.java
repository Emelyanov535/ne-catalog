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
			value = "select max(p.last_price) as priceEnd, min(p.last_price) as priceStart from product p " +
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

	@Query(value = """
		select distinct p.category 
		from product p 
		join product_ts_vector ptv on ptv.url = p.url
		where ptv.product_name @@ to_tsquery(:searchQuery)
		""", nativeQuery = true)
	List<Category> getSearchCategories(@Param("searchQuery") String searchQuery);
}