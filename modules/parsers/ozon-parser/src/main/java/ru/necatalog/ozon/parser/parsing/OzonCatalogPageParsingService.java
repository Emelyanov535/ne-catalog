package ru.necatalog.ozon.parser.parsing;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NotFoundException;
import ru.necatalog.ozon.parser.parsing.creator.OzonCategoryPageDataCreator;
import ru.necatalog.ozon.parser.parsing.dto.catalog.OzonCatalogPageData;
import ru.necatalog.ozon.parser.parsing.dto.catalog.OzonCatalogPaginator;
import ru.necatalog.ozon.parser.parsing.dto.catalog.OzonProductEntityCreator;
import ru.necatalog.ozon.parser.parsing.dto.catalog.ProductsInfo;
import ru.necatalog.ozon.parser.parsing.enumeration.OzonCategory;
import ru.necatalog.ozon.parser.service.OzonProductService;
import ru.necatalog.ozon.parser.utils.OzonConsts;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Category;

@Slf4j
@RequiredArgsConstructor
public class OzonCatalogPageParsingService {

    private static final Map<Category, Set<String>> URL_CACHE = new ConcurrentHashMap<>();

    private final OzonPageFetcher pageFetcher;

    private final OzonCategoryPageDataCreator categoryPageDataCreator;

    private final OzonProductEntityCreator productEntityCreator;

    private final ObjectMapper objectMapper;

    private final OzonProductService productService;

    @PostConstruct
    public void init() {
        for (Category category : Category.values()) {
            URL_CACHE.put(category, ConcurrentHashMap.newKeySet());
        }
    }

    public void parse(OzonCategory category) {
        String jsonInHtml;
        OzonCatalogPageData categoryPageData;
        ProductsInfo productsInfo;
        OzonCatalogPaginator paginator = null;
        boolean needRetry = false;
        String prevPage = null;
        do {
            try {
                if (paginator == null) {
                    jsonInHtml = pageFetcher.fetchPageJson(category.getCategoryUrl(), 500);
                } else {
                    jsonInHtml = pageFetcher.fetchPageJson(OzonConsts.OZON_API_LINK + paginator.getNextPage(), 500);
                }
                categoryPageData = categoryPageDataCreator.create(jsonInHtml);
                log.info(categoryPageData.getWidgetStates().getPaginator() + " --- " + categoryPageData.getNextPage());
                if (categoryPageData.getWidgetStates().getProductsInfo() != null) {
                    productsInfo = objectMapper.readValue(
                        categoryPageData.getWidgetStates().getProductsInfo(),
                        ProductsInfo.class);
                    var productEntities = productEntityCreator.createProductEntities(productsInfo, category);
                    log.info("Обнаружено {} товаров на странице", productEntities.size());
                    productService.save(productEntities.stream()
                        .filter(this::isNotDuplicate)
                        .collect(Collectors.toList()));
                }
                paginator = categoryPageData.getWidgetStates().getPaginator() != null ? objectMapper.readValue(
                    categoryPageData.getWidgetStates().getPaginator(),
                    OzonCatalogPaginator.class)
                    : new OzonCatalogPaginator("", categoryPageData.getNextPage());
                if (prevPage != null
                    && prevPage.equals(paginator.getNextPage())
                    || categoryPageData.getNextPage() != null && categoryPageData.getNextPage().split("\\?")[0].contains("null")) {
                    break;
                }
                prevPage = paginator.getNextPage();
            } catch (NotFoundException nfe) {
                needRetry = !needRetry;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } while (needRetry || paginator != null && paginator.getNextPage() != null);
        log.info("Завершили обработку {}", category);
        URL_CACHE.get(Category.valueOf(category.name())).clear();
    }

    private boolean isNotDuplicate(ProductEntity productEntity) {
        boolean newProduct = URL_CACHE.get(productEntity.getCategory()).add(productEntity.getUrl());
        if (URL_CACHE.get(productEntity.getCategory()).size() >= 100000) {
            URL_CACHE.get(productEntity.getCategory()).clear();
        }
        if (!newProduct) {
            log.debug("Найден дубликат товара {}", productEntity.getUrl());
        }
        return newProduct;
    }

}
