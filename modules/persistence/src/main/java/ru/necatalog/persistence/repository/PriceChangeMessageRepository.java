package ru.necatalog.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.PriceChangeMessage;

@Repository
public interface PriceChangeMessageRepository extends JpaRepository<PriceChangeMessage, Long> {
	List<PriceChangeMessage> findAllByProcessedFalse();

	@Modifying
	@Query("UPDATE PriceChangeMessage p SET p.processed = true WHERE p IN :messages")
	void markMessagesAsProcessed(@Param("messages") List<PriceChangeMessage> messages);
}
