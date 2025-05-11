package ru.necatalog.ozon.parser.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.ozon.parser.parsing.dto.ParsedData;
import ru.necatalog.ozon.parser.service.dto.ParseOzonCharacteristicPayload;
import ru.necatalog.persistence.entity.DelayedTaskEntity;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.entity.id.PriceHistoryId;
import ru.necatalog.persistence.enumeration.DelayedTaskStatus;
import ru.necatalog.persistence.enumeration.DelayedTaskType;
import ru.necatalog.persistence.repository.DelayedTaskRepository;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@Slf4j
@RequiredArgsConstructor
public class OzonProductService {

    private final ProductRepository productRepository;

    private final ProductPriceRepository productPriceRepository;

    private final DelayedTaskRepository delayedTaskRepository;

    private final ObjectMapper objectMapper;

    private PriceHistoryEntity getPriceHistory(ParsedData product) {
        var priceHistoryId = new PriceHistoryId();
        priceHistoryId.setProductUrl(product.getUrl());
        priceHistoryId.setDate(ZonedDateTime.now());
        var priceHistory = new PriceHistoryEntity();
        priceHistory.setId(priceHistoryId);
        priceHistory.setPrice(product.getPrice());
        return priceHistory;
    }

    private PriceHistoryEntity getPriceHistory(ProductEntity productEntity) {
        var priceHistoryId = new PriceHistoryId();
        priceHistoryId.setProductUrl(productEntity.getUrl());
        priceHistoryId.setDate(ZonedDateTime.now());
        var priceHistory = new PriceHistoryEntity();
        priceHistory.setId(priceHistoryId);
        priceHistory.setPrice(productEntity.getLastPrice());
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
        productEntity.setLastPrice(product.getPrice());
        return productEntity;
    }

    @SneakyThrows
    @Transactional
    public Optional<ProductEntity> save(ParsedData product) {
        Optional<ProductEntity> productEntity = Optional.empty();
        if (!productRepository.existsById(product.getUrl())) {
            productEntity = Optional.of(productRepository.save(getProduct(product)));
        } else {
            delayedTaskRepository.save(new DelayedTaskEntity(
                null,
                DelayedTaskStatus.NEW,
                DelayedTaskType.PARSE_OZON_CHARACTERISTIC,
                objectMapper.writeValueAsString(new ParseOzonCharacteristicPayload(product.getUrl(), product.getCategory())),
                LocalDateTime.now()));
        }
        productPriceRepository.save(getPriceHistory(product));
        return productEntity;
    }

    @Transactional
    public void save(List<ProductEntity> products) {
        List<ProductEntity> productsToSave = new ArrayList<>();
        List<PriceHistoryEntity> priceHistoriesToSave = new ArrayList<>();
        List<DelayedTaskEntity> delayedTaskToSave = new ArrayList<>();
        for (ProductEntity p : products) {
            if (!productRepository.existsById(p.getUrl())) {
                productsToSave.add(p);
                delayedTaskToSave.add(new DelayedTaskEntity(
                    null,
                    DelayedTaskStatus.NEW,
                    DelayedTaskType.PARSE_OZON_CHARACTERISTIC,
                    getJsonb(p),
                    LocalDateTime.now()));
            }
            priceHistoriesToSave.add(getPriceHistory(p));
        }
        productRepository.saveAll(productsToSave);
        productPriceRepository.saveAll(priceHistoriesToSave);
        delayedTaskRepository.saveAll(delayedTaskToSave);
        log.info("Сохранили {} продуктов", productsToSave.size());
    }

    @SneakyThrows
    private String getJsonb(ProductEntity p) {
        return objectMapper.writeValueAsString(new ParseOzonCharacteristicPayload(p.getUrl(), p.getCategory()));
    }

}
