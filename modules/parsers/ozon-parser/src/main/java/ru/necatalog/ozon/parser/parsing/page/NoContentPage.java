package ru.necatalog.ozon.parser.parsing.page;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public class NoContentPage {

    private static final String ERROR_TEXT_XPATH = "\"//*[contains(text(), 'Простите, произошла ошибка. Попробуйте обновить страницу или вернуться на шаг назад.')]\"";
    private static final String NOT_FOUND_TEXT_XPATH = "\"//*[contains(text(), 'По вашим параметрам ничего не нашлось. Попробуйте сбросить фильтры. ')]\"";
    private static final String SEARCH_RESULTS_ERROR = "div[data-widget='searchResultsError']";

    private final By errorText = By.xpath(ERROR_TEXT_XPATH);
    private final By notFoundText = By.xpath(NOT_FOUND_TEXT_XPATH);
    private final By searchResultsError = By.cssSelector(SEARCH_RESULTS_ERROR);

    private WebDriver driver;

    private WebDriverWait wait;

    public NoContentPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isLoaded() {
        try {
            return driver.findElement(searchResultsError) != null
                || driver.findElement(errorText) != null
                || driver.findElement(notFoundText) != null;
        } catch (Exception e) {
            return false;
        }
    }

}
