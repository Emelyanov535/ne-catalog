package ru.necatalog.wildberriesparser.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WildberriesProductService {
	private final ProductRepository productRepository;
	private final ProductPriceRepository productPriceRepository;

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
}

