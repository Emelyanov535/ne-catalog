package ru.necatalog.ozonparser.config;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.necatalog.ozonparser.config.properties.OzonParserConfigProperties;
import ru.necatalog.ozonparser.parser.pool.WebDriverPool;
import ru.necatalog.ozonparser.parser.service.parsing.OzonHtmlFetcher;
import ru.necatalog.ozonparser.parser.service.parsing.OzonPageParser;
import ru.necatalog.ozonparser.parser.service.parsing.OzonParsingService;
import ru.necatalog.ozonparser.parser.service.parsing.PageScroller;
import ru.necatalog.ozonparser.parser.service.scheduler.OzonProductUpdater;
import ru.necatalog.ozonparser.service.OzonProductService;
import ru.necatalog.ozonparser.service.mapper.OzonPriceHistoryMapper;
import ru.necatalog.ozonparser.service.mapper.OzonProductMapper;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@Configuration
@EnableConfigurationProperties(OzonParserConfigProperties.class)
@ConditionalOnProperty(prefix = "ozon-parser", name = "enabled", havingValue = "true")
public class OzonParserConfig {

    @Bean
    public WebDriverPool webDriverPool(ObjectFactory<WebDriver> webDriverFactory,
                                       OzonParserConfigProperties ozonParserConfigProperties) {
        return new WebDriverPool(webDriverFactory, ozonParserConfigProperties);
    }

    @Bean
    public PageScroller pageScroller() {
        return new PageScroller();
    }

    @Bean
    public OzonHtmlFetcher ozonHtmlFetcher(WebDriverPool webDriverPool,
                                           PageScroller pageScroller) {
        return new OzonHtmlFetcher(webDriverPool, pageScroller);
    }

    @Bean
    public OzonPageParser ozonPageParser() {
        return new OzonPageParser();
    }

    @Bean
    public OzonProductMapper ozonProductMapper() {
        return new OzonProductMapper();
    }

    @Bean
    public OzonPriceHistoryMapper ozonPriceHistoryMapper() {
        return new OzonPriceHistoryMapper();
    }

    @Bean
    public OzonProductService productService(ProductRepository productRepository,
                                             ProductPriceRepository productPriceRepository,
                                             OzonProductMapper productMapper,
                                             OzonPriceHistoryMapper priceHistoryMapper) {
        return new OzonProductService(productRepository, productPriceRepository, productMapper, priceHistoryMapper);
    }

    @Bean
    public OzonParsingService ozonParsingService(OzonHtmlFetcher ozonHtmlFetcher,
                                                 OzonParserConfigProperties ozonParserConfigProperties,
                                                 OzonPageParser ozonPageParser,
                                                 OzonProductService productService) {
        return new OzonParsingService(ozonHtmlFetcher, ozonParserConfigProperties, ozonPageParser, productService);
    }

    @Bean
    public OzonProductUpdater ozonProductUpdater(OzonParsingService ozonParsingService) {
        return new OzonProductUpdater(ozonParsingService);
    }

}
