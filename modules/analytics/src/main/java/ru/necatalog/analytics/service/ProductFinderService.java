package ru.necatalog.analytics.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductFinderService {

	private final ProductRepository productRepository;

	public List<ProductEntity> findSimilarProducts(String searchString) {
		return productRepository.findSimilarProducts(searchString);
	}
}
