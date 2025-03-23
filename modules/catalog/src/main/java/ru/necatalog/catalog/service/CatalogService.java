package ru.necatalog.catalog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.ProductRepository;


@Service
@RequiredArgsConstructor
public class CatalogService {
	private final ProductRepository productRepository;

	public Page<ProductEntity> getListProduct(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
		return productRepository.findAll(pageable);
	}

	public ProductEntity getProductById(String url) {
		return productRepository.findById(url).orElseThrow(() -> new RuntimeException("Product not found"));
	}
}
