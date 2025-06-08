package ru.necatalog.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.dto.ProductAttributeFilter;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.entity.id.ProductAttributeId;

import java.util.List;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttributeEntity, ProductAttributeId> {

	List<ProductAttributeEntity> findAllById_productUrlAndId_attributeIdIn(String productUrl, List<Long> list);

	@Query(nativeQuery = true,
			value = "select distinct p.attribute_id, p.value, p.unit from product_attribute p " +
					"where p.attribute_id in :categoryAttributes")
	List<ProductAttributeFilter> findDistinctById_attributeIdIn(@Param("categoryAttributes") List<Long> categoryAttributes);

	@Query("""
			select distinct p
			from ProductEntity p
			join ProductAttributeEntity pa on p.url = pa.id.productUrl
			where pa.value in (
			    select pa2.value
			    from ProductAttributeEntity pa2
			    where pa2.id.productUrl = :productUrl
			      and pa2.id.attributeId in (
			        select a.id
			        from AttributeEntity a
			        where a.group = :group
			      )
			) and p.url <> :productUrl
			""")
	List<ProductEntity> getIdenticalProductUrls(@Param("productUrl") String productUrl, @Param("group") String group);


	@Query("""
		select p
		from ProductAttributeEntity p
		where p.id.productUrl = :productUrl and p.id.attributeId in 
				(
			        select a.id
			        from AttributeEntity a
			        where a.group = :group
			      )
		""")
	List<ProductAttributeEntity> existsByCode(@Param("productUrl") String productUrl, @Param("group") String group);

	@Query("""
				SELECT pa FROM ProductAttributeEntity pa
				WHERE pa.id.productUrl = :productUrl AND pa.id.attributeId IN (
					SELECT a.id FROM AttributeEntity a WHERE a.group = :groupName
				)
			""")
	List<ProductAttributeEntity> findByProductUrlAndGroup(@Param("productUrl") String productUrl,
														  @Param("groupName") String groupName);

	@Query("""
			    SELECT DISTINCT p
			    FROM ProductEntity p
			    WHERE LOWER(p.brand) = LOWER(:brand)
			    AND p.url <> :originalUrl
				AND p.brand IS NOT NULL
			""")
	List<ProductEntity> findByBrandExcludeUrl(@Param("brand") String brand,
											  @Param("originalUrl") String originalUrl);


	@Query("""
    select distinct p
    from ProductEntity p
    join ProductAttributeEntity pa on p.url = pa.id.productUrl
    where pa.value = :code
""")
	List<ProductEntity> findByCode(@Param("code") String code);

	List<ProductAttributeEntity> findById_ProductUrl(String productUrl);
}
