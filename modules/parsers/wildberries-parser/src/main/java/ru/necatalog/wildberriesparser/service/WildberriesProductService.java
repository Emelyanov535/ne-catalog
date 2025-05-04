package ru.necatalog.wildberriesparser.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.persistence.entity.PriceChangeMessage;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.PriceChangeMessageRepository;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WildberriesProductService {
	private final ProductRepository productRepository;
	private final ProductPriceRepository productPriceRepository;
	private final PriceChangeMessageRepository priceChangeMessageRepository;

	@Transactional(readOnly = true)
	public List<ProductEntity> getProductWithoutAttributes() {
		return productRepository.getProductsWithoutAttributes();
	}

	@Transactional
	public void saveData(List<ProductEntity> productEntities, List<PriceHistoryEntity> priceHistoryEntities) {
		List<String> urls = productEntities.stream()
				.map(ProductEntity::getUrl)
				.collect(Collectors.toList());

		List<String> existingUrls = productRepository.findAllByUrlIn(urls).stream()
				.map(ProductEntity::getUrl)
				.toList();

		List<ProductEntity> uniqueProducts = productEntities.stream()
				.filter(product -> !existingUrls.contains(product.getUrl()))
				.collect(Collectors.toList());

		productRepository.saveAll(uniqueProducts);

		// Мапа продуктов по URL (обновленная после возможного saveAll)
		Map<String, ProductEntity> productMap = productRepository.findAllByUrlIn(urls).stream()
				.collect(Collectors.toMap(ProductEntity::getUrl, product -> product));

		// Обновляем productUrl в PriceHistoryEntity и lastPrice в ProductEntity
		for (PriceHistoryEntity priceHistory : priceHistoryEntities) {
			String url = priceHistory.getId().getProductUrl();
			ProductEntity product = productMap.get(url);

			if (product != null) {
				priceHistory.getId().setProductUrl(product.getUrl());
				product.setLastPrice(priceHistory.getPrice());
			}
		}

		// Обновляем lastPrice у всех продуктов
		productRepository.saveAll(productMap.values());

		// Сохраняем историю цен
		productPriceRepository.saveAll(priceHistoryEntities);
	}


	public void addMessageToDb(List<PriceHistoryEntity> priceHistories) {
		for (PriceHistoryEntity priceHistory : priceHistories) {
			// Получаем продукт из истории цен
			String productUrl = priceHistory.getId().getProductUrl();
			BigDecimal currentPrice = priceHistory.getPrice();

			// Находим последнюю цену для данного продукта
			PriceHistoryEntity lastPriceHistory = productPriceRepository.findLatestPriceByProductUrl(productUrl);

			// Проверяем, изменилась ли цена
			if (lastPriceHistory != null && lastPriceHistory.getPrice().compareTo(currentPrice) != 0) {
				// Если цена изменилась, создаем сообщение о изменении цены
				ProductEntity product = productRepository.findByUrl(productUrl).orElseThrow(); // Получаем продукт по URL

				PriceChangeMessage priceChangeMessage = PriceChangeMessage.builder()
						.product(product)
						.oldPrice(lastPriceHistory.getPrice())
						.newPrice(currentPrice)
						.createdAt(LocalDateTime.now())
						.processed(false)
						.build();

				// Сохраняем сообщение о изменении цены
				priceChangeMessageRepository.save(priceChangeMessage);
			}
		}
	}
}

