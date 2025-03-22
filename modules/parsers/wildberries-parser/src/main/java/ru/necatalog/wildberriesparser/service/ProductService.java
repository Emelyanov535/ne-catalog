package ru.necatalog.wildberriesparser.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@Service("wildberriesProductService")
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;

    @Transactional
    public void saveData(List<ProductEntity> productEntities, List<PriceHistoryEntity> priceHistoryEntities) {
        // Получаем URL продуктов
        List<String> urls = productEntities.stream()
                .map(ProductEntity::getUrl)
                .collect(Collectors.toList());

        // Находим уже существующие URL в базе данных
        List<String> existingUrls = productRepository.findAllByUrlIn(urls).stream()
                .map(ProductEntity::getUrl)
                .toList();

        // Фильтруем уникальные продукты, которых еще нет в базе
        List<ProductEntity> uniqueProducts = productEntities.stream()
                .filter(product -> !existingUrls.contains(product.getUrl()))
                .collect(Collectors.toList());

        // Сохраняем только новые продукты
        productRepository.saveAll(uniqueProducts);

        // Создаем мапу для быстрого доступа к продуктам по URL
        Map<String, ProductEntity> productMap = productRepository.findAllByUrlIn(urls).stream()
                .collect(Collectors.toMap(ProductEntity::getUrl, product -> product));

        // Фильтруем и обновляем идентификаторы для истории цен
        List<PriceHistoryEntity> updatedPriceHistories = priceHistoryEntities.stream()
                .peek(priceHistory -> {
                    ProductEntity product = productMap.get(priceHistory.getId().getProductUrl());
                    priceHistory.getId().setProductUrl(product.getUrl());
                })
                .collect(Collectors.toList());

        // Сохраняем историю цен
        productPriceRepository.saveAll(updatedPriceHistories);
    }
}

