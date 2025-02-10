package ru.necatalog.ozonparser.parser.service.parsing;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import ru.necatalog.ozonparser.config.properties.OzonParserConfigProperties;
import ru.necatalog.ozonparser.parser.enumeration.OzonCategory;
import ru.necatalog.ozonparser.parser.service.dto.ParsedData;
import ru.necatalog.ozonparser.service.OzonProductService;

@Slf4j
public class OzonParsingService {

    private final Map<String, Set<String>> urlCache;

    private final ExecutorService pageExecutorService;

    private final Semaphore semaphore;

    private final OzonHtmlFetcher categoryPageParsingService;

    private final OzonParserConfigProperties ozonConfigProperties;

    private final OzonPageParser ozonPageParser;

    private final OzonProductService productService;

    public OzonParsingService(OzonHtmlFetcher categoryPageParsingService,
                              OzonParserConfigProperties ozonConfigProperties, OzonPageParser ozonPageParser,
                              OzonProductService productService) {
        this.pageExecutorService = Executors.newFixedThreadPool(ozonConfigProperties.getMaxThreads());
        this.semaphore = new Semaphore(ozonConfigProperties.getMaxThreads());
        this.urlCache = new ConcurrentHashMap<>();
        for (OzonCategory category : OzonCategory.values()) {
            urlCache.put(category.getCategoryUrl(), ConcurrentHashMap.newKeySet());
        }

        this.categoryPageParsingService = categoryPageParsingService;
        this.ozonConfigProperties = ozonConfigProperties;
        this.ozonPageParser = ozonPageParser;
        this.productService = productService;
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
            String pageSource = categoryPageParsingService.fetchPageHtml(pageUrl, lastPageInCategory);
            List<ParsedData> parsedProducts =
                ozonPageParser.parseProductsFromCategoryPage(pageSource, category.getMappedCategory());
            log.info("""
                
                КОНЕЦ ПАРСИНГА СТРАНИЦЫ КАТЕГОРИИ
                КОЛИЧЕСТВО НАЙДЕННЫХ ТОВАРОВ НА СТРАНИЦЕ {},
                
                """, parsedProducts.size());
            if (urlCache.size() > 1000000) {
                urlCache.clear();
            }
            Set<String> categoryCachecUrl = urlCache.get(category.getCategoryUrl());
            List<ParsedData> uniqueData = parsedProducts.stream()
                .filter(data -> categoryCachecUrl.add(data.getUrl()))
                .toList();
            productService.saveBatch(uniqueData);
        } finally {
            MDC.clear();
            semaphore.release();
        }
    }

}