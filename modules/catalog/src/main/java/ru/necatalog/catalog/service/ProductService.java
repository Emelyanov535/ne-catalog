package ru.necatalog.catalog.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopAdditionalInfoAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopDisplayAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopGpuAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopProcessorAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopRamAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopStorageAttribute;
import ru.necatalog.persistence.repository.ProductAttributeRepository;
import ru.necatalog.persistence.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductAttributeRepository productAttributeRepository;

	private static final Double SIMILARITY_THRESHOLD = 0.5;

	private List<ProductEntity> findProductListByBrand(String brand, String productUrl) {
		return productAttributeRepository
				.findByBrandExcludeUrl(brand, productUrl);
	}

	public List<ProductEntity> findIdenticalProducts(String productUrl) {
		Set<ProductEntity> result = new LinkedHashSet<>();

		ProductEntity originalProduct = productRepository.findByUrl(productUrl)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		if (originalProduct.getBrand().isBlank()) {
			return new ArrayList<>(result);
		}

		String brand = originalProduct.getBrand();
		String originalTitle = originalProduct.getProductName();
		List<ProductEntity> brandList = findProductListByBrand(brand, productUrl);
		List<ProductAttributeEntity> originalProductCodes = productAttributeRepository.existsByCode(productUrl, LaptopAdditionalInfoAttribute.VENDOR_CODE.getGroupName());

		Set<String> originalCodes = originalProductCodes.stream()
				.map(ProductAttributeEntity::getValue)
				.filter(code -> code != null && !code.isBlank())
				.collect(Collectors.toSet());

		if (!originalCodes.isEmpty()) {
			List<ProductEntity> byCode = productAttributeRepository
					.getIdenticalProductUrls(productUrl, LaptopAdditionalInfoAttribute.VENDOR_CODE.getGroupName());

			result.addAll(byCode);

			brandList.stream()
					.filter(p -> {
						String title = p.getProductName();
						return title != null && originalCodes.stream().anyMatch(title::contains);
					})
					.forEach(result::add);
		} else {
			// продукты со схожим названием
			List<ProductEntity> sameByProductName = brandList.stream()
					.filter(productEntity -> calculateSimilarity(productEntity.getProductName(), originalTitle) > SIMILARITY_THRESHOLD)
					.toList();

			// список кодов продуктов со схожим названием
			List<ProductAttributeEntity> sameByProductNameCodes = sameByProductName.stream()
					.flatMap(p -> productAttributeRepository
							.existsByCode(p.getUrl(), LaptopAdditionalInfoAttribute.VENDOR_CODE.getGroupName())
							.stream())
					.filter(p -> !Objects.equals(p.getValueType(), "NUMBER"))
					.toList();

			// есть ли в названии продукта такой код, если есть, то мы нашли код производителя
			List<ProductAttributeEntity> test = sameByProductNameCodes.stream()
					.filter(code -> {
						String vendorCode = code.getValue();
						return vendorCode != null && originalTitle.contains(vendorCode);
					}).toList();

			// точный код производителя
			Set<String> sameCodes = test.stream()
					.map(ProductAttributeEntity::getValue)
					.filter(code -> code != null && !code.isBlank())
					.collect(Collectors.toSet());

			if (sameCodes.isEmpty()) {
				return new ArrayList<>(result);
			}

			List<ProductEntity> sameProductsByCode = productAttributeRepository.findByCode((String) Arrays.stream(sameCodes.toArray()).findFirst().get());
			result.addAll(sameProductsByCode);

			brandList.stream()
					.filter(p -> {
						String title = p.getProductName();
						return title != null && sameCodes.stream().anyMatch(title::contains);
					})
					.forEach(result::add);
		}

		return new ArrayList<>(result);
	}

	private double calculateSimilarity(String s1, String s2) {
		if (s1 == null || s2 == null) return 0.0;
		return new JaroWinklerSimilarity().apply(s1.toLowerCase(), s2.toLowerCase());
	}

	private List<ProductEntity> getIdenticalProductsByAttribute(String brand, String productUrl) {
		List<String> keyGroups = List.of(
				LaptopDisplayAttribute.DIAGONAL.getGroupName(),
				LaptopGpuAttribute.GPU_NAME.getGroupName(),
				LaptopProcessorAttribute.CPU_NAME.getGroupName(),
				LaptopRamAttribute.RAM.getGroupName(),
				LaptopStorageAttribute.TYPE.getGroupName()
		);

		Map<String, List<ProductAttributeEntity>> originalAttributesByGroup = keyGroups.stream()
				.collect(Collectors.toMap(
						group -> group,
						group -> productAttributeRepository.findByProductUrlAndGroup(productUrl, group)
				));

		List<ProductEntity> brandCandidates = productAttributeRepository
				.findByBrandExcludeUrl(brand, productUrl);

		List<ProductEntity> similarProducts = new ArrayList<>();

		JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

		for (ProductEntity candidate : brandCandidates) {
			int totalMatches = 0;
			int totalAttributes = 0;

			for (String group : keyGroups) {
				List<ProductAttributeEntity> originalAttrs = originalAttributesByGroup.get(group);
				if (originalAttrs.isEmpty()) continue;

				List<ProductAttributeEntity> candidateAttrs = productAttributeRepository.findByProductUrlAndGroup(candidate.getUrl(), group);
				totalAttributes += originalAttrs.size();

				totalMatches += (int) originalAttrs.stream()
						.filter(origAttr -> candidateAttrs.stream()
								.anyMatch(candAttr ->
										candAttr.getId().getAttributeId().equals(origAttr.getId().getAttributeId()) &&
												similarity.apply(candAttr.getValue(), origAttr.getValue()) >= 0.90
								)).count();
			}

			if (totalAttributes == 0) continue;

			double similarityPercentage = (double) totalMatches / totalAttributes * 100;

			if (similarityPercentage >= 75) {
				similarProducts.add(candidate);
			}
		}

		return similarProducts;
	}

}
