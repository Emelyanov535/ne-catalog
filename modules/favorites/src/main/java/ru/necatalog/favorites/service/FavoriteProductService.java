package ru.necatalog.favorites.service;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.necatalog.auth.service.AccountService;
import ru.necatalog.persistence.entity.FavoriteProductEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.repository.FavoriteProductRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {
	private final FavoriteProductRepository favoriteProductRepository;
	private final ProductRepository productRepository;
	private final AccountService accountService;

	@Transactional
	public void addProductToFavorite(String productUrl) {
		final UserEntity userEntity = accountService.getCurrentUser();

		if (favoriteProductRepository.existsByUserIdAndProductUrl(userEntity.getId(), productUrl)) {
			return;
		}

		ProductEntity product = productRepository.findByUrl(productUrl)
				.orElseThrow(() -> new EntityNotFoundException("Product not found"));

		FavoriteProductEntity favoriteProductEntity = FavoriteProductEntity.builder()
				.user(userEntity)
				.product(product)
				.createdAt(LocalDateTime.now())
				.addedPrice(product.getLastPrice())
				.build();

		favoriteProductRepository.save(favoriteProductEntity);
	}

	@Transactional
	public void removeProductFromFavorite(String productUrl) {
		final UserEntity userEntity = accountService.getCurrentUser();
		FavoriteProductEntity favoriteProductEntity = favoriteProductRepository.findByUserIdAndProductUrl(userEntity.getId(), productUrl);
		favoriteProductRepository.deleteById(favoriteProductEntity.getId());
	}

	@Transactional
	public List<ProductEntity> getFavoriteProducts() {
		final UserEntity userEntity = accountService.getCurrentUser();

		return userEntity.getFavoriteProducts().stream()
				.map(FavoriteProductEntity::getProduct)
				.toList();
	}

	@Transactional
	public Page<ProductEntity> getFavoriteProductsWithPaging(int page, int size, String sort) {
		Pageable pageable = PageRequest.of(
				page, size,
				sort.equals("DESC")
						? Sort.by(Sort.Direction.DESC, "createdAt")
						: Sort.by(Sort.Direction.ASC, "createdAt")
		);

		UserEntity userEntity = accountService.getCurrentUser();

		Page<FavoriteProductEntity> favoritePage = favoriteProductRepository.findAllByUser(userEntity, pageable);

		List<ProductEntity> products = favoritePage
				.map(FavoriteProductEntity::getProduct)
				.getContent();

		return new PageImpl<>(products, pageable, favoritePage.getTotalElements());
	}
}
