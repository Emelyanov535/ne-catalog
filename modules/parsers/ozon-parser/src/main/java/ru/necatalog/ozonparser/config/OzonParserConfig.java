package ru.necatalog.ozonparser.config;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.necatalog.ozonparser.config.properties.OzonParserProperties;
import ru.necatalog.ozonparser.parser.pool.WebDriverPool;
import ru.necatalog.ozonparser.parser.service.parsing.OzonHtmlFetcher;
import ru.necatalog.ozonparser.parser.service.parsing.OzonPageParser;
import ru.necatalog.ozonparser.parser.service.parsing.OzonParsingService;
import ru.necatalog.ozonparser.parser.service.parsing.PageScroller;
import ru.necatalog.ozonparser.parser.service.processor.AttributeProcessor;
import ru.necatalog.ozonparser.parser.service.scheduler.OzonProductUpdater;
import ru.necatalog.ozonparser.service.OzonProductService;
import ru.necatalog.ozonparser.service.mapper.OzonPriceHistoryMapper;
import ru.necatalog.ozonparser.service.mapper.OzonProductMapper;
import ru.necatalog.persistence.repository.ProductAttributeRepository;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@Configuration
public class OzonParserConfig {

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public WebDriverPool webDriverPool(ObjectFactory<WebDriver> webDriverFactory,
                                       OzonParserProperties ozonParserConfigProperties) {
        return new WebDriverPool(webDriverFactory, ozonParserConfigProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public PageScroller pageScroller() {
        return new PageScroller();
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonHtmlFetcher ozonHtmlFetcher(WebDriverPool webDriverPool,
                                           PageScroller pageScroller) {
        return new OzonHtmlFetcher(webDriverPool, pageScroller);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonPageParser ozonPageParser() {
        return new OzonPageParser();
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonProductMapper ozonProductMapper() {
        return new OzonProductMapper();
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonPriceHistoryMapper ozonPriceHistoryMapper() {
        return new OzonPriceHistoryMapper();
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonProductService productService(ProductRepository productRepository,
                                             ProductPriceRepository productPriceRepository) {
        return new OzonProductService(productRepository, productPriceRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonParsingService ozonParsingService(OzonHtmlFetcher ozonHtmlFetcher,
                                                 OzonParserProperties ozonParserConfigProperties,
                                                 OzonPageParser ozonPageParser,
                                                 OzonProductService productService,
                                                 ObjectProvider<AttributeProcessor> attributeProcessors,
                                                 ProductAttributeRepository productAttributeRepository) {
        return new OzonParsingService(
            ozonHtmlFetcher,
            ozonParserConfigProperties,
            ozonPageParser,
            productService,
            attributeProcessors.stream().toList(),
            productAttributeRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "ozon-parser.enabled", havingValue = "true")
    public OzonProductUpdater ozonProductUpdater(OzonParsingService ozonParsingService,
                                                 ProductRepository productRepository) {
        return new OzonProductUpdater(ozonParsingService, productRepository);
    }

}
