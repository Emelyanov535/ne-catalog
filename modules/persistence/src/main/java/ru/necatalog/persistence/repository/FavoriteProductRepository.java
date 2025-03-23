package ru.necatalog.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.FavoriteProductEntity;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProductEntity, Long> {
	@Query(value = """
		select fp from FavoriteProductEntity as fp
		where fp.user.id = :userId and fp.product.url = :productUrl
		""")
	FavoriteProductEntity findByUserIdAndProductUrl(@Param("userId") Long userId, @Param("productUrl") String productUrl);

	boolean existsByUserIdAndProductUrl(Long userId, String productUrl);
}
