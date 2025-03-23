package ru.necatalog.ozonparser.parser.service.page;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static ru.necatalog.ozonparser.utils.OzonConsts.OZON_MAIN_LINK;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.necatalog.common.exception.ParsingException;
import ru.necatalog.ozonparser.parser.service.dto.ParsedData;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;

@Slf4j
public class OzonCategoryPage {

    private static final String ORIGINAL_TEXT = "Оригинал";

    private static final String SEARCH_RESULTS_CSS_SELECTOR = "div[data-widget='searchResultsV2']";

    private static final String PRODUCT_URL_SELECTOR = "a:first-of-type";

    private static final String PRODUCT_IMAGE_SELECTOR = "a:first-of-type > div > div:first-of-type img";

    private static final String PRODUCT_BRAND_SELECTOR = "div:first-of-type > div:nth-of-type(2) > span:first-of-type";

    private static final String PRODUCT_NAME_SELECTOR = "div:first-of-type > a:first-of-type > div:first-of-type > span:first-of-type";

    private static final String PRODUCT_PRICE_SELECTOR = "div:first-of-type > div:first-of-type > span:first-of-type";

    private static final String LEFT_COUNT = "div:first-of-type > div:nth-of-type(2) > span:first-of-type";

    private final Document document;

    public OzonCategoryPage(String pageHtml) {
        this.document = Jsoup.parse(pageHtml);
    }

    public Stream<ParsedData> getProducts(Category category) {
        Elements searchResultsDivs = getSearchResultsDivs();
        if (searchResultsDivs.isEmpty()) {
            return Stream.of();
        }
        log.info("нашли столько результатов на странице {}", searchResultsDivs.size());

        return searchResultsDivs.stream()
            .flatMap(searchResultsDiv -> {
                Elements productsDivs = getProductsDivs(searchResultsDiv);
                return extractParsedData(productsDivs, category);
            });
    }

    private Elements getSearchResultsDivs() {
        try {
            return document.select(SEARCH_RESULTS_CSS_SELECTOR);
        } catch (Exception e) {
            log.warn("Не удалось достать блоки searchResultsV2");
            return new Elements();
        }
    }

    private Elements getProductsDivs(Element searchResultsDiv) {
        return searchResultsDiv.select("> div > div");
    }

    AtomicInteger productsCount = new AtomicInteger();

    private Stream<ParsedData> extractParsedData(Elements productDivs,
                                                 Category category) {
        log.info("Суммарно спарсили {} товаров", productsCount.addAndGet(productDivs.size()));
        return productDivs.stream()
            .map(productDiv -> {
                try {
                    if (isAbsent(productDiv)) {
                        log.info("Нет в наличии");
                        return null;
                    }
                    removeExtraInfo(productDiv);
                    return getProductData(productDiv, category);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
                return null;
            })
            .filter(Objects::nonNull);
    }

    private boolean isAbsent(Element productDiv) {
        return productDiv.selectFirst("div[title='Нет в наличии']") != null
            || productDiv.getElementsByTag("a").isEmpty();
    }

    @SuppressWarnings("all")
    private void removeExtraInfo(Element productDiv) {
        try {
            Element leftCountBlock = productDiv.selectFirst(LEFT_COUNT);
            if (leftCountBlock == null) {
                return;
            }
            if (leftCountBlock.text().contains("Осталось")) {
                leftCountBlock.parent().remove();
            }
        } catch (Throwable ignored) {
            // ignored
        }
    }

    private ParsedData getProductData(Element productDiv,
                                      Category category) {
        return ParsedData.builder()
            .category(category)
            .marketplace(Marketplace.OZON)
            .url(extractUrl(productDiv))
            .imageUrl(extractImageUrl(productDiv))
            .brand(extractBrand(productDiv))
            .productName(extractProductName(productDiv))
            .price(extractPrice(productDiv))
            .build();
    }

    private String extractUrl(Element productDiv) {
        Element productUrlBlock = productDiv.selectFirst(PRODUCT_URL_SELECTOR);
        if (productUrlBlock == null) {
            throw new ParsingException("Не обнаружен блок ссылки!");
        }
        return OZON_MAIN_LINK + replaceExtraParamsFromUrl(productUrlBlock.attr("href"));
    }

    private String replaceExtraParamsFromUrl(String url) {
        return url.replaceAll("\\?.*$", "");
    }

    private String extractImageUrl(Element productDiv) {
        Element productImageBlock = productDiv.selectFirst(PRODUCT_IMAGE_SELECTOR);
        if (productImageBlock == null) {
            throw new ParsingException("Не обнаружен блок с изображением товара!");
        }
        return productImageBlock.attr("src");
    }

    private String extractBrand(Element productDiv) {
        Element productBrandBlock = productDiv.selectFirst(PRODUCT_BRAND_SELECTOR);
        if (productBrandBlock == null) {
            throw new ParsingException("Не обнаружен блок с названием бренда!");
        }
        return ORIGINAL_TEXT.equals(productBrandBlock.text()) ? "" : productBrandBlock.text();
    }

    private String extractProductName(Element productDiv) {
        Element productNameBlock = productDiv.selectFirst(PRODUCT_NAME_SELECTOR);
        if (productNameBlock == null) {
            throw new ParsingException("Не обнаружен блок c названием товара!");
        }
        return productNameBlock.text();
    }

    private BigDecimal extractPrice(Element productDiv) {
        Element productPriceBlock = productDiv.selectFirst(PRODUCT_PRICE_SELECTOR);
        if (productPriceBlock == null) {
            throw new ParsingException("Не обнаружен блок c ценой товара!");
        }
        return parseOzonPriceToBigDecimal(productPriceBlock.text());
    }

    private BigDecimal parseOzonPriceToBigDecimal(String ozonPrice) {
        String cleanedString = ozonPrice.replaceAll("\\D", "");
        return new BigDecimal(cleanedString);
    }

}
