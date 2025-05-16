package ru.necatalog.favorites.web.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.favorites.service.FavoriteProductService;
import ru.necatalog.persistence.entity.ProductEntity;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
@Tag(name = "Избранное", description = "Управление избранными товарами")
@SecurityRequirement(name = "bearerAuth")
public class FavouriteProductController {
	private final FavoriteProductService favoriteProductService;

	@GetMapping
	@Operation(summary = "Добавление товара в избранное")
	public void addFavouriteProduct(@RequestParam("productUrl") String productUrl) {
		favoriteProductService.addProductToFavorite(productUrl);
	}

	@DeleteMapping
	@Operation(summary = "Удаление товара из избранного")
	public void removeProductFromFavorite(@RequestParam("productUrl") String productUrl) {
		favoriteProductService.removeProductFromFavorite(productUrl);
	}

	@GetMapping("/list")
	@Operation(summary = "Получение списка избранных товаров")
	public ResponseEntity<List<ProductEntity>> getProductList() {
		return ResponseEntity.ok(favoriteProductService.getFavoriteProducts());
	}

	@GetMapping("/paging")
	@Operation(summary = "Получение списка избранных товаров с пагинацией")
	public ResponseEntity<Page<ProductEntity>> getProductList(
			@RequestParam(defaultValue = "0", value = "page") int page,
			@RequestParam(defaultValue = "10", value = "size") int size,
			@RequestParam(defaultValue = "ASC", value = "sort") String sort) {
		return ResponseEntity.ok(favoriteProductService.getFavoriteProductsWithPaging(page, size, sort));
	}
}
