package ru.necatalog.ozon.parser.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.necatalog.ozon.parser.config.properties.OzonParserProperties;
import ru.necatalog.ozon.parser.parsing.OzonCatalogPageParsingService;
import ru.necatalog.ozon.parser.parsing.OzonCharacteristicsPageParsingService;
import ru.necatalog.ozon.parser.parsing.OzonPageFetcher;
import ru.necatalog.ozon.parser.parsing.OzonParsingService;
import ru.necatalog.ozon.parser.parsing.creator.OzonCategoryPageDataCreator;
import ru.necatalog.ozon.parser.parsing.creator.OzonCharacteristicsPageDataCreator;
import ru.necatalog.ozon.parser.parsing.dto.catalog.OzonProductEntityCreator;
import ru.necatalog.ozon.parser.parsing.pool.WebDriverPool;
import ru.necatalog.ozon.parser.parsing.processor.AttributeProcessor;
import ru.necatalog.ozon.parser.parsing.scheduler.OzonCatalogParsingScheduler;
import ru.necatalog.ozon.parser.parsing.scheduler.OzonCharacteristicParsingScheduler;
import ru.necatalog.ozon.parser.service.OzonProductService;
import ru.necatalog.persistence.repository.DelayedTaskRepository;
import ru.necatalog.persistence.repository.ProductAttributeRepository;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;
import ru.necatalog.persistence.service.DelayedTaskService;

@Configuration
public class OzonParserConfig {

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public WebDriverPool ozonWebDriverPool(ObjectFactory<WebDriver> webDriverFactory,
                                       OzonParserProperties ozonParserConfigProperties) {
        return new WebDriverPool(webDriverFactory, ozonParserConfigProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonPageFetcher ozonHtmlFetcher(WebDriverPool webDriverPool) {
        return new OzonPageFetcher(webDriverPool);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonProductService ozonProductService(ProductRepository productRepository,
                                             ProductPriceRepository productPriceRepository,
                                             DelayedTaskRepository delayedTaskRepository,
                                             ObjectMapper objectMapper) {
        return new OzonProductService(productRepository, productPriceRepository, delayedTaskRepository, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonParsingService ozonParsingService(OzonParserProperties ozonParserConfigProperties,
                                                 OzonCatalogPageParsingService categoryPageParsingService,
                                                 ObjectMapper objectMapper,
                                                 OzonCharacteristicsPageParsingService characteristicsPageParsingService,
                                                 DelayedTaskService delayedTaskService) {
        return new OzonParsingService(
            ozonParserConfigProperties,
            categoryPageParsingService,
            delayedTaskService,
            objectMapper,
            characteristicsPageParsingService);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonCatalogParsingScheduler ozonProductUpdater(OzonParsingService ozonParsingService) {
        return new OzonCatalogParsingScheduler(ozonParsingService);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonCharacteristicParsingScheduler ozonProductCharacteristicsUpdater(OzonParsingService ozonParsingService) {
        return new OzonCharacteristicParsingScheduler(ozonParsingService);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonCategoryPageDataCreator ozonCatalogParsingService(ObjectMapper objectMapper) {
        return new OzonCategoryPageDataCreator(objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonCharacteristicsPageDataCreator ozonCharacteristicsPageDataCreator(ObjectMapper objectMapper) {
        return new OzonCharacteristicsPageDataCreator(objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonProductEntityCreator ozonProductEntityCreator() {
        return new OzonProductEntityCreator();
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonCatalogPageParsingService ozonCategoryPageParsingService(OzonPageFetcher pageFetcher,
                                                                        OzonCategoryPageDataCreator categoryPageDataCreator,
                                                                        OzonProductEntityCreator productEntityCreator,
                                                                        ObjectMapper objectMapper,
                                                                        OzonProductService productService) {
        return new OzonCatalogPageParsingService(
            pageFetcher, categoryPageDataCreator, productEntityCreator, objectMapper, productService);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonCharacteristicsPageParsingService ozonCharacteristicsPageParsingService(
        ProductAttributeRepository productAttributeRepository,
        ObjectProvider<AttributeProcessor> attributeProcessors,
        OzonPageFetcher pageFetcher,
        ObjectMapper objectMapper,
        OzonCharacteristicsPageDataCreator characteristicsPageDataCreator) {
        return new OzonCharacteristicsPageParsingService(
            productAttributeRepository, attributeProcessors.stream().toList(), pageFetcher, characteristicsPageDataCreator, objectMapper);
    }

}
