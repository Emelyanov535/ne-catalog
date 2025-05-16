package ru.necatalog.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.FavoriteProductEntity;
import ru.necatalog.persistence.entity.UserEntity;

import java.util.List;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProductEntity, Long> {
	@Query(value = """
			select fp from FavoriteProductEntity as fp
			where fp.user.id = :userId and fp.product.url = :productUrl
			""")
	FavoriteProductEntity findByUserIdAndProductUrl(@Param("userId") Long userId, @Param("productUrl") String productUrl);

	boolean existsByUserIdAndProductUrl(Long userId, String productUrl);

	Page<FavoriteProductEntity> findAllByUser(UserEntity user, Pageable pageable);

	@Query("SELECT fp FROM FavoriteProductEntity fp " +
			"JOIN FETCH fp.product p " +
			"JOIN FETCH fp.user u " +
			"WHERE p.lastPrice IS NOT NULL AND fp.addedPrice IS NOT NULL")
	List<FavoriteProductEntity> findAllWithRecentPrices();
}
