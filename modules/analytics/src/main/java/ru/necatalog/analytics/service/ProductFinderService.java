package ru.necatalog.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.ProductRepository;
import ru.necatalog.search.service.SearchService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFinderService {

	private final ProductRepository productRepository;

	public List<ProductEntity> findSimilarProducts(String searchString) {
		return productRepository.findSimilarProducts(searchString);
	}
}
