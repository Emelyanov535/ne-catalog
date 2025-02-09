package ru.necatalog.ozonparser.service;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.ozonparser.parser.service.dto.ParsedData;
import ru.necatalog.ozonparser.service.dto.PriceHistoryDto;
import ru.necatalog.ozonparser.service.dto.ProductDto;
import ru.necatalog.ozonparser.service.dto.ProductsPageDto;
import ru.necatalog.ozonparser.service.mapper.OzonPriceHistoryMapper;
import ru.necatalog.ozonparser.service.mapper.OzonProductMapper;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.PriceHistoryId;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@Slf4j
@RequiredArgsConstructor
public class OzonProductService {

    private final ProductRepository productRepository;

    private final ProductPriceRepository productPriceRepository;

    private final OzonProductMapper productMapper;

    private final OzonPriceHistoryMapper priceHistoryMapper;

    @Transactional
    @Retryable
    public void saveBatch(List<ParsedData> parsedData) {
        List<String> productsUrls = parsedData.stream().map(ParsedData::getUrl).toList();
        List<String> alreadySavedUrls = productRepository.findSavedUrl(productsUrls);
        List<ProductEntity> products = parsedData.stream()
            .filter(data -> !alreadySavedUrls.contains(data.getUrl()))
            .map(this::getProduct)
            .toList();
        List<PriceHistoryEntity> prices = parsedData.stream().map(this::getPriceHistory).toList();
        productRepository.saveAll(products);
        log.info("Сохранили пачку товаров {}", products.size());
        productPriceRepository.saveAll(prices);
        log.info("Сохранили историю цен {}", prices.size());
    }

    @Transactional(readOnly = true)
    public ProductDto findByUrl(String productUrl) {
        var product = productRepository.findByUrl(productUrl).orElseThrow(EntityNotFoundException::new);
        return productMapper.toProductDto(product);
    }

    @Transactional(readOnly = true)
    public PriceHistoryDto findPriceHistoryByRange(String productUrl,
                                                   ZonedDateTime from,
                                                   ZonedDateTime to) {
        var priceHistory = productPriceRepository
            .findAllById_ProductUrlAndIdDateAfterAndId_DateBeforeOrderById_DateAsc(productUrl, from, to);
        return priceHistoryMapper.toPriceHistoryDto(priceHistory);
    }

    @Transactional(readOnly = true)
    public ProductsPageDto findAllProductsByPage(Marketplace marketplace,
                                                 Category category,
                                                 Pageable pageable) {
        var page = productRepository.findAllByMarketplaceAndCategory(marketplace, category, pageable);
        return new ProductsPageDto(
            page.getNumberOfElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getContent().stream().map(productMapper::toProductDto).toList()
        );
    }

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

}
