package ru.necatalog.favorites.service;

import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

		FavoriteProductEntity favoriteProductEntity = FavoriteProductEntity.builder()
				.user(userEntity)
				.product(productRepository.findById(productUrl).orElseThrow())
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
	public Page<ProductEntity> getFavoriteProductsWithPaging(int page, int size) {

		Pageable pageable = PageRequest.of(page, size);

		final UserEntity userEntity = accountService.getCurrentUser();

		List<ProductEntity> favoriteProducts = userEntity.getFavoriteProducts().stream()
				.map(FavoriteProductEntity::getProduct)
				.toList();

		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), favoriteProducts.size());

		List<ProductEntity> pagedList = favoriteProducts.subList(start, end);

		return new PageImpl<>(pagedList, pageable, favoriteProducts.size());
	}

}
