package ru.ulstu.catalog.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.ulstu.catalog.service.CatalogService;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
@Tag(name = "Каталог", description = "Работа с каталогом")
public class CatalogController {
	private final CatalogService catalogService;

	@GetMapping
	@Operation(summary = "Получить каталог всех товаров")
	public Page<ProductEntity> getProducts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		return catalogService.getListProduct(page, size);
	}
}
