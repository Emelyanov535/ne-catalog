package ru.necatalog.ozonparser.service;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.ozonparser.parser.service.dto.ParsedData;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.entity.id.PriceHistoryId;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@Slf4j
@RequiredArgsConstructor
public class OzonProductService {

    private final ProductRepository productRepository;

    private final ProductPriceRepository productPriceRepository;

    private PriceHistoryEntity getPriceHistory(ParsedData product) {
        var priceHistoryId = new PriceHistoryId();
        priceHistoryId.setProductUrl(product.getUrl());
        priceHistoryId.setDate(ZonedDateTime.now());
        var priceHistory = new PriceHistoryEntity();
        priceHistory.setId(priceHistoryId);
        priceHistory.setPrice(product.getPrice());
        return priceHistory;
    }

    private ProductEntity getProduct(ParsedData product) {
        var productEntity = new ProductEntity();
        productEntity.setCategory(product.getCategory());
        productEntity.setBrand(product.getBrand());
        productEntity.setProductName(product.getProductName());
        productEntity.setUrl(product.getUrl());
        productEntity.setMarketplace(product.getMarketplace());
        productEntity.setImageUrl(product.getImageUrl());
        return productEntity;
    }

    AtomicInteger productsCount = new AtomicInteger();
    @Transactional
    public Optional<ProductEntity> save(ParsedData product) {
        Optional<ProductEntity> productEntity = Optional.empty();
        if (!productRepository.existsByUrl(product.getUrl())) {
            productEntity = Optional.of(productRepository.save(getProduct(product)));
        }
        productPriceRepository.save(getPriceHistory(product));
        log.info("Сохранили историю цены суммарно {}", productsCount.addAndGet(1));
        return productEntity;
    }
}
