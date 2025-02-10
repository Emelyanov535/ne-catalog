package ru.necatalog.ozonparser.parser.service.page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.necatalog.ozonparser.parser.service.dto.ParsedData;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;

@Slf4j
public class OzonCategoryPage {

    private static final String OZON_MAIN_LINK = "https://www.ozon.ru";

    public static final String SEARCH_RESULTS_CSS_SELECTOR = "div[data-widget='searchResultsV2']";

    public static final int INDEX_OF_EXTRA_DIV_IF_SALE_PRODUCT = 1;

    public static final int INDEX_OF_PRODUCT_PRICE = 0;

    public static final int INDEX_OF_PRODUCT_BRAND = 1;

    public static final int INDEX_OF_PRODUCT_NAME = 2;

    private final Document document;

    public OzonCategoryPage(String pageHtml) {
        this.document = Jsoup.parse(pageHtml);
    }

    public List<ParsedData> getProducts(Category category) {
        List<ParsedData> products = new ArrayList<>();

        Elements searchResultsDivs = getSearchResultsDivs();
        if (searchResultsDivs.isEmpty()) {
            return List.of();
        }
        log.info("нашли столько результатов на странице {}", searchResultsDivs.size());

        for (Element searchResultsDiv : searchResultsDivs) {
            Elements productsDivs = getProductsDivs(searchResultsDiv);
            List<Elements> allProductDataDivs = getAllProductDataDivs(productsDivs);
            List<ParsedData> parsedProductsData = extractParsedData(allProductDataDivs, category);
            products.addAll(parsedProductsData);
        }

        /*try {

            for (Element searchResultsDiv : searchResultsDivs) {
                var productDivs = searchResultsDiv.select("> div > div");
                for (Element productDiv : productDivs) {
                    Elements productDataDivs = productDivs.select("> div > *");
                    if (productDataDivs.select("> *").isEmpty()) {
                        continue;
                    }
                    productDataDivs.removeLast();
                    Element productUrlAndImageUrlA = productDataDivs.first();
                    Element productDataDiv = productDataDivs.last();
                    Elements productDataInnerDivs = productDataDiv.select("> *");
                    try {
                        if (productDataInnerDivs.get(INDEX_OF_EXTRA_DIV_IF_SALE_PRODUCT)
                            .select("span").text().toLowerCase()
                            .contains("осталось")) {
                            productDataInnerDivs.remove(INDEX_OF_EXTRA_DIV_IF_SALE_PRODUCT);
                        }
                    } catch (Exception ignored) {}

                    Elements productBrandBlockSpans = productDataInnerDivs.get(INDEX_OF_PRODUCT_BRAND).select("> span");

                    String productUrl = OZON_MAIN_LINK + productUrlAndImageUrlA.attr("href").replaceAll("\\?.*$", "");
                    String productImageUrl = productUrlAndImageUrlA.select("> div > div")
                        .first().getElementsByTag("img")
                        .first().attr("src");

                    BigDecimal productPrice;
                    try {
                        productPrice = parseOzonPriceToBigDecimal(
                            productDataInnerDivs.get(INDEX_OF_PRODUCT_PRICE).select("> div > span")
                                .first().text());
                    } catch (Exception e) {
                        log.error("не удалось распарсить цену");
                        continue;
                    }

                    String productBrand = productBrandBlockSpans.first().selectFirst("> span > b").text();
                    String productName = productDataInnerDivs.get(INDEX_OF_PRODUCT_NAME).select("> div > span").text();

                    ParsedData parsedData = new ParsedData();
                    parsedData.setCategory(category);
                    parsedData.setMarketplace(Marketplace.OZON);
                    parsedData.setUrl(productUrl);
                    parsedData.setImageUrl(productImageUrl);
                    parsedData.setPrice(productPrice);
                    parsedData.setBrand(productBrand);
                    parsedData.setProductName(productName);
                    products.add(parsedData);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }*/
        return products;
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

    private List<Elements> getAllProductDataDivs(Elements productsDivs) {
        List<Elements> allProductDataDivs = new ArrayList<>();
        for (Element productDiv : productsDivs) {
            Elements productDataDivs = productDiv.select("> div > *");
            if (productDataDivs.select("> *").isEmpty()) {
                continue;
            }
            removeAddInFavouriteDiv(productDataDivs);
            allProductDataDivs.add(productDataDivs);
        }
        return allProductDataDivs;
    }

    private void removeAddInFavouriteDiv(Elements productDataDivs) {
//        productDataDivs.removeLast();
    }

    private List<ParsedData> extractParsedData(List<Elements> allProductDataDivs,
                                               Category category) {
        List<ParsedData> parsedData = new ArrayList<>();
        for (Elements productDataDivs : allProductDataDivs) {
            try {
                ParsedData parsedDataItem = getParsedDataItem(productDataDivs, category);
                parsedData.add(parsedDataItem);
            } catch (Exception e) {
                //log.error(e.getMessage(), e);
            }
        }
        return parsedData;
    }

    private ParsedData getParsedDataItem(Elements productDataDivs,
                                         Category category) {
        removeExtraDivIfExists(productDataDivs);
        return ParsedData.builder()
            .category(category)
            .marketplace(Marketplace.OZON)
            .url(extractUrl(productDataDivs))
            .imageUrl(extractImageUrl(productDataDivs))
            .brand(extractBrand(productDataDivs))
            .productName(extractProductName(productDataDivs))
            .price(extractPrice(productDataDivs))
            .build();
    }

    private void removeExtraDivIfExists(Elements productDataDivs) {
        Element productDataDiv = productDataDivs.last();
        Elements productDataInnerDivs = productDataDiv.select("> *");
        try {
            if (productDataInnerDivs.get(INDEX_OF_EXTRA_DIV_IF_SALE_PRODUCT)
                .select("span").text().toLowerCase()
                .contains("осталось")) {
                productDataInnerDivs.remove(INDEX_OF_EXTRA_DIV_IF_SALE_PRODUCT);
            }
        } catch (Exception ignored) {}
    }

    private String extractUrl(Elements productDataDivs) {
        Element productUrlA = productDataDivs.first();
        return OZON_MAIN_LINK + productUrlA
            .attr("href").replaceAll("\\?.*$", "");
    }

    private String extractImageUrl(Elements productDataDivs) {
        Element productImageUrlA = productDataDivs.first();
        return productImageUrlA.select("> div > div")
            .first().getElementsByTag("img")
            .first().attr("src");
    }

    private String extractBrand(Elements productDataDivs) {
        Elements productDataInnerDivs = getProductMainDataInnerDivs(productDataDivs);
        //log.info(productDataInnerDivs.html());
        Elements productBrandBlockSpans = productDataInnerDivs.get(INDEX_OF_PRODUCT_BRAND)
            .select("> span");
        String brand = productBrandBlockSpans.first().selectFirst("> span > b").text();
        if (productBrandBlockSpans.size() == 1 && "Оригинал".equals(brand)) {
            return "БРЕНД_НЕ_УКАЗАН";
        }
        return brand;
    }

    private String extractProductName(Elements productDataDivs) {
        Elements productDataInnerDivs = getProductMainDataInnerDivs(productDataDivs);
        return productDataInnerDivs.get(INDEX_OF_PRODUCT_NAME)
            .select("> div > span").text();
    }

    private BigDecimal extractPrice(Elements productDataDivs) {
        Elements productDataInnerDivs = getProductMainDataInnerDivs(productDataDivs);
        return parseOzonPriceToBigDecimal(
            productDataInnerDivs.get(INDEX_OF_PRODUCT_PRICE).select("> div > span")
                .first().text());
    }

    private Elements getProductMainDataInnerDivs(Elements productDataDivs) {
        return productDataDivs.last().select("> *");
    }

    private BigDecimal parseOzonPriceToBigDecimal(String ozonPrice) {
        String cleanedString = ozonPrice.replaceAll("[^\\d]", "");
        return new BigDecimal(cleanedString);
    }

}
