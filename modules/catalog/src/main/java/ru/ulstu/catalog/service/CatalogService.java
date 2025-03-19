package ru.ulstu.catalog.service;

import jakarta.annotation.PostConstruct;
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

	@PostConstruct
	public void init() {
		System.out.println("CatalogService init");
	}
	private final ProductRepository productRepository;

	public Page<ProductEntity> getListProduct(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAll(pageable);
	}
}
