package ru.necatalog.ozonparser.parser.service.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import ru.necatalog.ozonparser.config.properties.OzonParserProperties;
import ru.necatalog.ozonparser.parser.enumeration.OzonCategory;
import ru.necatalog.ozonparser.parser.service.dto.Characteristic;
import ru.necatalog.ozonparser.parser.service.dto.ParsedData;
import ru.necatalog.ozonparser.parser.service.processor.AttributeProcessor;
import ru.necatalog.ozonparser.service.OzonProductService;
import ru.necatalog.ozonparser.utils.OzonConsts;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.repository.ProductAttributeRepository;

@Slf4j
public class OzonParsingService {

    private final Map<String, Set<String>> urlCache;

    private final ExecutorService pageExecutorService;

    private final Semaphore semaphore;

    private final OzonHtmlFetcher ozonHtmlFetcher;

    private final OzonParserProperties ozonConfigProperties;

    private final OzonPageParser ozonPageParser;

    private final OzonProductService productService;
    
    private final ProductAttributeRepository productAttributeRepository;

    private final Map<OzonCategory, AttributeProcessor> attributeProcessorsMap = new HashMap<>();

    public OzonParsingService(OzonHtmlFetcher categoryPageParsingService,
                              OzonParserProperties ozonConfigProperties,
                              OzonPageParser ozonPageParser,
                              OzonProductService productService,
                              List<AttributeProcessor> attributeProcessors,
                              ProductAttributeRepository productAttributeRepository) {
        this.pageExecutorService = Executors.newFixedThreadPool(1/*ozonConfigProperties.getMaxThreads()*/);
        this.semaphore = new Semaphore(ozonConfigProperties.getMaxThreads());
        this.productAttributeRepository = productAttributeRepository;
        this.urlCache = new ConcurrentHashMap<>();
        for (OzonCategory category : OzonCategory.values()) {
            urlCache.put(category.getCategoryUrl(), ConcurrentHashMap.newKeySet());
        }

        this.ozonHtmlFetcher = categoryPageParsingService;
        this.ozonConfigProperties = ozonConfigProperties;
        this.ozonPageParser = ozonPageParser;
        this.productService = productService;
        attributeProcessors.forEach(processor ->
            attributeProcessorsMap.put(processor.getCategory(), processor));
    }

    public void startProcessing() {
        for (OzonCategory category : OzonCategory.values()) {
            log.info("НАЧАЛО ОБРАБОТКИ КАТЕГОРИИ {}", category);
            processCategory(category);
        }
    }

    private void processCategory(OzonCategory category) {
        int pageIndex = 1;
        AtomicBoolean lastPageInCategory = new AtomicBoolean(false);
        while (!lastPageInCategory.get()) {
            try {
                semaphore.acquire();

                int finalPageIndex = pageIndex;
                String pageUrl = category.getCategoryUrl() + "&page=" + finalPageIndex;

                pageExecutorService.submit(() -> processCategoryPage(pageUrl, category, lastPageInCategory));

                pageIndex += ozonConfigProperties.getMaxNumOfPagesOnScreen();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (lastPageInCategory.get()) {
            log.info("Достигли последней страницы категории");
        }
    }

    private void processCategoryPage(String pageUrl,
                                     OzonCategory category,
                                     AtomicBoolean lastPageInCategory) {
        try {
            MDC.put("pageUrl", pageUrl);
            String pageSource = ozonHtmlFetcher.fetchCategoryPageHtml(pageUrl, lastPageInCategory);
            ozonPageParser.parseProductsFromCategoryPage(pageSource, category.getMappedCategory())
                .filter(this::isNotDuplicate)
                .forEach(product -> productService.save(product));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            MDC.clear();
            semaphore.release();
        }
    }

    public void processAttributePage(ProductEntity product) throws JsonProcessingException {
        String pageUrl = OzonConsts.OZON_API_LINK
            + "/entrypoint-api.bx/page/json/v2?abt_att=1&url="
            + product.getUrl().replace(OzonConsts.OZON_MAIN_LINK, "")
            + "?layout_container=pdpPage2column&layout_page_index=2";
        log.info("Запрашиваем json");
        String json = ozonHtmlFetcher.fetchPageJson(product, pageUrl);
        log.info("Получили json");
        List<Characteristic> characteristics = ozonPageParser.parseAttributesPage(json);
        List<ProductAttributeEntity> attributeEntities = attributeProcessorsMap.get(OzonCategory.valueOf(product.getCategory().name()))
            .process(characteristics, product.getUrl());
        log.info("Сохраняем атрибуты");
        productAttributeRepository.saveAll(attributeEntities);
    }

    private boolean isNotDuplicate(ParsedData product) {
        boolean newProduct = urlCache.get(product.getCategoryUrl()).add(product.getUrl());
        if (urlCache.get(product.getCategoryUrl()).size() >= 100000) {
            urlCache.get(product.getCategoryUrl()).clear();
        }
        if (!newProduct) {
            log.info("Дубликат {}", product.getUrl());
        }
        return newProduct;
    }

}