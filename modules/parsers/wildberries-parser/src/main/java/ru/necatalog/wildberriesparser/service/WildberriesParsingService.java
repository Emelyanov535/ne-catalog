package ru.necatalog.wildberriesparser.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.PriceHistoryEntity;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.entity.id.PriceHistoryId;
import ru.necatalog.persistence.repository.ProductAttributeRepository;
import ru.necatalog.wildberriesparser.processor.AttributeProcessor;
import ru.necatalog.wildberriesparser.service.client.Client;
import ru.necatalog.wildberriesparser.service.dto.ProductAttributesResponse;
import ru.necatalog.wildberriesparser.service.dto.ProductListDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class WildberriesParsingService {
	private static final int ELEMENTS_IN_PAGE = 100;

	private final Client client;
	private final ConversionService conversionService;
	private final WildberriesProductService wildberriesProductService;
	private final ProductAttributeRepository productAttributeRepository;
	private final AttributeProcessor attributeProcessor;

	public void parse() {
		int page = 1;
		Integer totalPages = null;

		do {
			ProductListDto pageData = client.scrapPage(page);
			if (pageData == null) {
				continue;
			}

			log.info("Получена страница: {}", page);

			if (totalPages == null) {
				totalPages = calculateTotalPages(pageData.getData().getTotal());
			}

			processPageData(pageData);

			page++;
		} while (page <= totalPages && page <= ELEMENTS_IN_PAGE);
	}

	private void processPageData(ProductListDto pageData) {
		List<ProductEntity> productEntities = new ArrayList<>();
		List<PriceHistoryEntity> priceHistories = new ArrayList<>();

		for (var dto : pageData.getData().getProducts()) {
			try {
				ProductEntity productEntity = conversionService.convert(dto, ProductEntity.class);

				assert productEntity != null;
				PriceHistoryEntity priceHistory = buildPriceHistory(productEntity.getUrl(), dto);

				productEntities.add(productEntity);
				priceHistories.add(priceHistory);
			} catch (Exception e) {
				log.error("Ошибка обработки продукта: {}", dto, e);
			}
		}

		wildberriesProductService.saveData(productEntities, priceHistories);
	}

	private PriceHistoryEntity buildPriceHistory(String productUrl, ProductListDto.Data.ProductInfoDto dto) {
		double price = extractPrice(dto);
		return PriceHistoryEntity.builder()
				.id(new PriceHistoryId(productUrl, ZonedDateTime.now()))
				.price(BigDecimal.valueOf(price))
				.build();
	}

	private double extractPrice(ProductListDto.Data.ProductInfoDto dto) {
		if (dto.getSizes() == null || dto.getSizes().isEmpty()) {
			throw new IllegalStateException("Нет доступных размеров для продукта: " + dto);
		}
		return dto.getSizes().getFirst().getPrice().getTotal() / 100.0;
	}

	private int calculateTotalPages(int totalElements) {
		return (int) Math.ceil((double) totalElements / ELEMENTS_IN_PAGE);
	}

	public void parseAttributes() {
		// Получаем товары, у которых нет записи в таблице аттрибутов
		List<ProductEntity> productList = wildberriesProductService.getProductWithoutAttributes();

		Map<String, String> mapProductUrlAndAttributesUrl = productList.stream()
				.collect(Collectors.toMap(
						ProductEntity::getUrl,
						productEntity -> productEntity.getImageUrl().replace("images/big/1.webp", "info/ru/card.json")
				));

		mapProductUrlAndAttributesUrl.forEach((key, value) -> {
			ProductAttributesResponse productData = client.scrapAttributes(value);

			if (productData == null) {
				log.warn("Skipping product with URL: {} due to failed attribute fetch.", key);

				// Пишем в файл
				try {
					Files.writeString(
							Paths.get("skipped_products.txt"),
							key + System.lineSeparator(),
							StandardOpenOption.CREATE, StandardOpenOption.APPEND
					);
				} catch (IOException e) {
					log.error("Failed to write skipped URL to file for product: {}", key, e);
				}

				return; // пропускаем
			}


			List<ProductAttributeEntity> attributeEntities = attributeProcessor.process(productData, key);

			productAttributeRepository.saveAll(attributeEntities);

		});
	}
}
