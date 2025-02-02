package ru.ulstu.parsingservice.service;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ulstu.parsingservice.enumeration.Category;
import ru.ulstu.parsingservice.enumeration.Marketplace;
import ru.ulstu.parsingservice.ozon_parser.service.dto.ParsedData;
import ru.ulstu.parsingservice.persistence.entity.PriceHistoryEntity;
import ru.ulstu.parsingservice.persistence.entity.PriceHistoryId;
import ru.ulstu.parsingservice.persistence.entity.ProductEntity;
import ru.ulstu.parsingservice.persistence.repository.ProductPriceRepository;
import ru.ulstu.parsingservice.persistence.repository.ProductRepository;
import ru.ulstu.parsingservice.service.dto.PriceHistoryDto;
import ru.ulstu.parsingservice.service.dto.ProductDto;
import ru.ulstu.parsingservice.service.dto.ProductsPageDto;
import ru.ulstu.parsingservice.service.mapper.PriceHistoryMapper;
import ru.ulstu.parsingservice.service.mapper.ProductMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductPriceRepository productPriceRepository;

    private final ProductMapper productMapper;

    private final PriceHistoryMapper priceHistoryMapper;

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
