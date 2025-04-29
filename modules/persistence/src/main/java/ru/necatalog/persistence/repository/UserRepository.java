package ru.necatalog.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUsername(String login);

	@Query(value = """
		select u
		from UserEntity u
		join u.favoriteProducts fp
		where u.isNotification = true and fp.product.url = :productUrl
		""")
	List<UserEntity> findAllNotificatedUsers(@Param("productUrl") String productUrl);
}
