package ru.necatalog.analytics.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.necatalog.analytics.service.ProductFinderService;
import ru.necatalog.persistence.repository.ProductRepository;
import ru.necatalog.persistence.repository.projection.SimilarProductData;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFinderServiceImpl implements ProductFinderService {

	private final ProductRepository productRepository;

	@Override
	public List<SimilarProductData> getProductsInfo(String productUrl) {
		return productRepository.findSimilarProducts(productUrl);
	}
}
