package ru.necatalog.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.id.ProductAttributeId;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttributeEntity, ProductAttributeId> {
}
