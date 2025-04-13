package ru.necatalog.catalog.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.catalog.service.CatalogService;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
@Tag(name = "Каталог", description = "Работа с каталогом")
public class CatalogController {
	private final CatalogService catalogService;

	@GetMapping
	@Operation(summary = "Получить каталог всех товаров")
	public Page<ProductEntity> getProducts(
			@RequestParam(defaultValue = "0", value = "page") int page,
			@RequestParam(defaultValue = "10", value = "size") int size
	) {
		return catalogService.getListProduct(page, size);
	}

	@GetMapping("/getByUrl")
	@Operation(summary = "Получить информацию о товаре по ссылке")
	public ProductEntity getProduct(@RequestParam(value = "url") String url) {
		return catalogService.getProductById(url);
	}
}
