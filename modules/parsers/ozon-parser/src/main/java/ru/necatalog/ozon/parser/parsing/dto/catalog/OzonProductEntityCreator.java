package ru.necatalog.ozon.parser.parsing.dto.catalog;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import ru.necatalog.ozon.parser.parsing.enumeration.OzonCategory;
import ru.necatalog.ozon.parser.utils.OzonConsts;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;

@Slf4j
@RequiredArgsConstructor
public class OzonProductEntityCreator {

    public Set<ProductEntity> createProductEntities(ProductsInfo productsInfo,
                                                    OzonCategory category) {
        return productsInfo.getProductsData().stream()
            .map(pd -> createProductEntity(pd, category))
            .collect(Collectors.toSet());
    }

    private ProductEntity createProductEntity(OzonCatalogPageProductData categoryPageData,
                                              OzonCategory category) {
        return ProductEntity.builder()
            .category(Category.valueOf(category.name()))
            .marketplace(Marketplace.OZON)
            .createdAt(LocalDateTime.now())
            .percentChange(0.0)
            .productName(StringEscapeUtils.unescapeHtml4(categoryPageData.getProductName().trim())
                .replaceAll("^\\s*<b>\\s*", "").replaceAll("</b>$", ""))
            .url(OzonConsts.OZON_MAIN_LINK + categoryPageData.getUrl().replaceAll("\\?.*$", ""))
            .brand(StringEscapeUtils.unescapeHtml4(categoryPageData.getBrand().trim())
                .replaceAll("^\\s*<b>\\s*", "") .replaceAll("</b>$", ""))
            .lastPrice(StringUtils.isNotBlank(categoryPageData.getPrice()) ? new BigDecimal(StringEscapeUtils.unescapeHtml4(categoryPageData.getPrice().trim())
                .replaceAll("\\D", "")) : BigDecimal.ZERO)
            .imageUrl(categoryPageData.getImageUrl())
            .build();
    }

}
