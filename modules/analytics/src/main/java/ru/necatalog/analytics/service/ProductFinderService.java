package ru.necatalog.analytics.service;

import ru.necatalog.analytics.service.dto.ProductInfoDto;
import ru.necatalog.persistence.repository.projection.SimilarProductData;

import java.util.List;

public interface ProductFinderService {
	List<SimilarProductData> getProductsInfo(String productUrl);
}
