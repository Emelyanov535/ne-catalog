package ru.necatalog.favorites.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.favorites.service.FavoriteProductService;
import ru.necatalog.persistence.entity.ProductEntity;
import java.util.List;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
@Tag(name = "Избранное", description = "Управление избранными товарами")
@SecurityRequirement(name = "bearerAuth")
public class FavouriteProductController {
	private final FavoriteProductService favoriteProductService;

	@GetMapping("/{productId}")
	@Operation(summary = "Добавление товара в избранное")
	public void addFavouriteProduct(@PathVariable("productId") Long productId) {
		favoriteProductService.addProductToFavorite(productId);
	}

	@DeleteMapping("/{productId}")
	@Operation(summary = "Удаление товара из избранного")
	public void removeProductFromFavorite(@PathVariable("productId") Long productId) {
		favoriteProductService.removeProductFromFavorite(productId);
	}

	@GetMapping("/list")
	@Operation(summary = "Получение списка избранных товаров")
	public ResponseEntity<List<ProductEntity>> getProductList() {
		return ResponseEntity.ok(favoriteProductService.getFavoriteProducts());
	}
}
