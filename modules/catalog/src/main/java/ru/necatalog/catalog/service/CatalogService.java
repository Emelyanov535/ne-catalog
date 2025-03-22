package ru.necatalog.catalog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.ProductRepository;


@Service
@RequiredArgsConstructor
public class CatalogService {
	private final ProductRepository productRepository;

	public Page<ProductEntity> getListProduct(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAll(pageable);
	}

	public ProductEntity getProductById(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
	}
}
