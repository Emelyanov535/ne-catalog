package ru.necatalog.notifications.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.FavoriteProductEntity;
import ru.necatalog.persistence.entity.PriceChangeMessage;
import ru.necatalog.persistence.repository.FavoriteProductRepository;
import ru.necatalog.persistence.repository.PriceChangeMessageRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceDropNotificationService {

	private final FavoriteProductRepository favoriteProductRepository;
	private final PriceChangeMessageRepository priceChangeMessageRepository;

	@Transactional
	public void checkPriceDrops() {
		List<FavoriteProductEntity> favorites = favoriteProductRepository.findAllWithRecentPrices();

		favorites.forEach(favorite -> {
			BigDecimal currentPrice = favorite.getProduct().getLastPrice();
			BigDecimal addedPrice = favorite.getAddedPrice();
			BigDecimal lastNotifiedPrice = favorite.getLastNotifiedPrice();

			// Рассчитываем процент снижения от изначальной цены
			double priceDropPercent = calculateDropPercent(addedPrice, currentPrice);

			// Проверяем порог уведомления
			if (priceDropPercent >= favorite.getUser().getNotificationPercent() &&
					(lastNotifiedPrice == null || currentPrice.compareTo(lastNotifiedPrice) != 0)) {

				createNotification(favorite, addedPrice, currentPrice);
				favorite.setLastNotifiedPrice(currentPrice);
			}
		});

		favoriteProductRepository.saveAll(favorites);
	}

	private double calculateDropPercent(BigDecimal original, BigDecimal current) {
		return original.subtract(current)
				.divide(original, 4, RoundingMode.HALF_UP)
				.doubleValue() * 100;
	}

	private void createNotification(FavoriteProductEntity favorite,
									BigDecimal oldPrice,
									BigDecimal newPrice) {
		PriceChangeMessage message = PriceChangeMessage.builder()
				.product(favorite.getProduct())
				.user(favorite.getUser())
				.oldPrice(oldPrice)
				.newPrice(newPrice)
				.dropPercent(calculateDropPercent(oldPrice, newPrice))
				.createdAt(LocalDateTime.now())
				.processed(false)
				.build();

		priceChangeMessageRepository.save(message);
	}
}
