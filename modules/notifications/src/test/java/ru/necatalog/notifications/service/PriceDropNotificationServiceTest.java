package ru.necatalog.notifications.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.necatalog.persistence.entity.FavoriteProductEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.repository.FavoriteProductRepository;
import ru.necatalog.persistence.repository.PriceChangeMessageRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PriceDropNotificationServiceTest {

	@Mock
	private FavoriteProductRepository favoriteProductRepository;
	@Mock
	private PriceChangeMessageRepository priceChangeMessageRepository;

	@InjectMocks
	private PriceDropNotificationService priceDropNotificationService;

	@Test
	void checkPriceDrops_ShouldCreateNotification_WhenPriceDropExceedsThreshold() {
		UserEntity user = new UserEntity();
		user.setNotificationPercent(10);

		ProductEntity product = new ProductEntity();
		product.setLastPrice(BigDecimal.valueOf(80));

		FavoriteProductEntity favorite = new FavoriteProductEntity();
		favorite.setUser(user);
		favorite.setProduct(product);
		favorite.setAddedPrice(BigDecimal.valueOf(100));
		favorite.setLastNotifiedPrice(null);

		when(favoriteProductRepository.findAllWithRecentPrices()).thenReturn(List.of(favorite));

		priceDropNotificationService.checkPriceDrops();

		// Проверяем, что уведомление создано и цена сохранена
		verify(priceChangeMessageRepository, times(1)).save(any());
		verify(favoriteProductRepository).saveAll(any());
		assertEquals(BigDecimal.valueOf(80), favorite.getLastNotifiedPrice());
	}
}
