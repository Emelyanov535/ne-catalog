package ru.necatalog.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.dto.ProductAttributeFilter;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.id.ProductAttributeId;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttributeEntity, ProductAttributeId> {

    List<ProductAttributeEntity> findAllById_productUrlAndId_attributeIdIn(String productUrl, List<Long> list);

    @Query(nativeQuery = true,
        value = "select distinct p.attribute_id, p.value, p.unit from product_attribute p " +
            "where p.attribute_id in :categoryAttributes")
    List<ProductAttributeFilter> findDistinctById_attributeIdIn(@Param("categoryAttributes") List<Long> categoryAttributes);

}
