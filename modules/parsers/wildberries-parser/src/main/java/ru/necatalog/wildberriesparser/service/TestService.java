package ru.necatalog.wildberriesparser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.necatalog.notifications.PriceChangedEvent;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void checkPriceAndNotify(List<ProductEntity> productEntities, List<PriceHistoryEntity> priceHistoryEntities) {
        List<String> productUrls = productEntities.stream()
                .map(ProductEntity::getUrl)
                .toList();

        List<ProductEntity> existingProducts = productRepository.findAllByUrlIn(productUrls);
        Map<String, ProductEntity> productMap = existingProducts.stream()
                .collect(Collectors.toMap(ProductEntity::getUrl, product -> product));

        for (int i = 0; i < productEntities.size(); i++) {
            ProductEntity productEntity = productEntities.get(i);
            PriceHistoryEntity newPriceHistory = priceHistoryEntities.get(i);

            // Получаем старую цену из базы данных
            ProductEntity existingProduct = productMap.get(productEntity.getUrl());
            if (existingProduct != null) {
                // Получаем последнюю цену для этого товара
                BigDecimal lastPrice = productPriceRepository.findLatestPriceByProductUrl(existingProduct.getUrl());

                // Если новая цена отличается от последней, генерируем событие
                if (lastPrice == null || lastPrice.compareTo(newPriceHistory.getPrice()) != 0) {
                    // Генерация события об изменении цены
                    PriceChangedEvent event = new PriceChangedEvent(this, productEntity.getUrl(), lastPrice, newPriceHistory.getPrice());
                    eventPublisher.publishEvent(event);
                }
            }
        }
    }
}
