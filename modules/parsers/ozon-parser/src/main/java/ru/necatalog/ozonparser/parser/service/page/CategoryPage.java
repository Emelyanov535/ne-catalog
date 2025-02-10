package ru.necatalog.ozonparser.parser.service.page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.necatalog.ozonparser.parser.service.dto.ParsedData;

@Slf4j
public class CategoryPage implements MarketplacePage {

    private static final String SEARCH_RESULTS = "div[data-widget='searchResultsV2']";

    private final By searchResults = By.cssSelector(SEARCH_RESULTS);

    private final WebDriver driver;

    private final WebDriverWait wait;

    public CategoryPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public List<ParsedData> getParsedProducts() {
        wait.until(visibilityOfElementLocated(searchResults));
        log.info("Нашли SearchResultsV2");
        var searchResultsElement = driver.findElement(searchResults);
        wait.until(driver -> visibilityOfElementLocated(By.cssSelector(":scope > div")));
        log.info("Нашли внешний блок списка");
        var outerDiv = searchResultsElement.findElement(By.cssSelector(":scope > div")); // Внешний блок со списком товаров
        wait.until(driver -> visibilityOfAllElements(outerDiv.findElements(By.cssSelector(":scope > div"))));
        log.info("Нашли элементы списка");
        var innerDivs = outerDiv.findElements(By.cssSelector(":scope > div")); // Блок карточки товара

        var products = new ArrayList<ParsedData>();
        innerDivs.forEach(innerDiv -> {
            var productDataDivs = innerDiv.findElements(By.cssSelector(":scope > div"));
//            var productImageUrl = productDataDivs.get(0)
//                                                 .findElement(By.cssSelector(":scope > a > div"))
//                                                 .findElements(By.cssSelector(":scope > div")).getFirst()
//                                                 .findElement(By.tagName("img")).getAttribute("src");
//
//            var productBrand = productDataDivs.get(1).findElement(By.cssSelector(":scope > div"))
//                                              .findElements(By.cssSelector(":scope > div")).getFirst()
//                                              .findElement(By.tagName("b")).getText();
//
//            var productNameLink = productDataDivs.get(1).findElement(By.cssSelector(":scope > div > a"));
//
//            var productUrl = productNameLink.getAttribute("href");
//
//            var productName = productNameLink.findElement(By.tagName("span")).getText();
//
//            var productPrice = parseCurrency(productDataDivs.get(2).findElement(By.cssSelector(":scope > div > div"))
//                                              .findElements(By.tagName("span")).getFirst().getText());
            /*var parsedData = new ParsedData();
            parsedData.setUrl(productUrl);
            parsedData.setBrand(productBrand);
            parsedData.setProductName(productName);
            parsedData.setImageUrl(productImageUrl);
            parsedData.setPrice(productPrice);
            products.add(parsedData);*/
        });


        return products;
    }

    private BigDecimal parseCurrency(String currencyStr) {
        String cleanedString = currencyStr.replaceAll("[^\\d]", "");

        return new BigDecimal(cleanedString);
    }

    @Override
    public boolean isLoaded() {
        try {
            return driver.findElement(searchResults) != null;
        } catch (Exception e) {
            return false;
        }
    }

}
