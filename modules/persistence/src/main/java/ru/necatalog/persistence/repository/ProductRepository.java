package ru.necatalog.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByUrlIn(List<String> urls);

    @Query("""
        select p.url from ProductEntity p where p.url in :urls
    """)
    List<String> findSavedUrl(List<String> urls);

    Optional<ProductEntity> findByUrl(String url);

    Page<ProductEntity> findAllByMarketplaceAndCategory(Marketplace marketplace, Category category, Pageable pageable);

}
