package ru.necatalog.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.necatalog.analytics.web.dto.StatsData;
import ru.necatalog.catalog.service.ProductService;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Marketplace;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;
import ru.necatalog.persistence.repository.projection.PriceStatsData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisService {

	private final ProductPriceRepository productPriceRepository;
	private final ProductService productService;
	private final ProductRepository productRepository;

	public List<PriceStatsData> getHistoricalValue(String url) {
		return productPriceRepository.getDailyPriceStatsByProductUrls(List.of(url));
	}

	public List<StatsData> getHistoricalPricesForIdenticalProducts(String url) {
		ProductEntity entity = productRepository.findById(url).orElseThrow();

		List<ProductEntity> listIdenticalProducts = productService.findIdenticalProducts(url);

		List<ProductEntity> wbProducts = new ArrayList<>(listIdenticalProducts.stream()
				.filter(p -> p.getMarketplace().equals(Marketplace.WILDBERRIES))
				.toList());

		List<ProductEntity> ozonProducts = new ArrayList<>(listIdenticalProducts.stream()
				.filter(p -> p.getMarketplace().equals(Marketplace.OZON))
				.toList());

		List<String> identicalProductsUrls = new ArrayList<>(listIdenticalProducts.stream()
				.map(ProductEntity::getUrl)
				.toList());

		switch (entity.getMarketplace()) {
			case Marketplace.WILDBERRIES -> wbProducts.add(entity);
			case Marketplace.OZON -> ozonProducts.add(entity);
		}
		identicalProductsUrls.add(url);

		List<String> wbProductsUrls = wbProducts.stream()
				.map(ProductEntity::getUrl)
				.toList();

		List<String> ozonProductsUrls = ozonProducts.stream()
				.map(ProductEntity::getUrl)
				.toList();

		List<PriceStatsData> wbData = productPriceRepository.getDailyPriceStatsByProductUrls(wbProductsUrls);
		List<PriceStatsData> ozonData = productPriceRepository.getDailyPriceStatsByProductUrls(ozonProductsUrls);
		List<PriceStatsData> identicalData = productPriceRepository.getDailyPriceStatsByProductUrls(identicalProductsUrls);

		Map<LocalDate, StatsData> resultMap = new LinkedHashMap<>();

		processMarketplaceData(wbData, resultMap, MarketplaceType.WILDBERRIES);

		processMarketplaceData(ozonData, resultMap, MarketplaceType.OZON);

		processMarketplaceData(identicalData, resultMap, MarketplaceType.GENERAL);

		return resultMap.values().stream()
				.sorted(Comparator.comparing(StatsData::getDate))
				.toList();
	}

	private void processMarketplaceData(List<PriceStatsData> sourceData,
										Map<LocalDate, StatsData> resultMap,
										MarketplaceType marketplaceType) {
		for (PriceStatsData data : sourceData) {
			LocalDate date = data.getDate();

			StatsData statsData = resultMap.computeIfAbsent(date, d ->
					StatsData.builder()
							.date(d)
							.build());

			StatsData.MarketplaceData marketplaceData = StatsData.MarketplaceData.builder()
					.min(data.getMinPrice())
					.max(data.getMaxPrice())
					.avg(data.getAvgPrice())
					.build();

			switch (marketplaceType) {
				case WILDBERRIES -> statsData.setWildberries(marketplaceData);
				case OZON -> statsData.setOzon(marketplaceData);
				case GENERAL -> statsData.setGeneral(marketplaceData);
			}
		}
	}

	private enum MarketplaceType {
		WILDBERRIES, OZON, GENERAL
	}
}
