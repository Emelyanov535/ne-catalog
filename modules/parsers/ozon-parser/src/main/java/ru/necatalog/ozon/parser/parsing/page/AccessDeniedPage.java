package ru.necatalog.ozon.parser.parsing.page;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class AccessDeniedPage implements MarketplacePage {

    private static final String RELOAD_BUTTON_ID = "reload-button";
    private static final String RELOAD_BUTTON_XPATH = "//button[contains(text(),'Обновить')]";
    private static final String WARNING_IMAGE_CSS = "img[alt='warning']";
    private static final String ACCESS_DENIED_TEXT_XPATH = "//h1[text()='Доступ ограничен']";

    private final By reloadButtonById = By.id(RELOAD_BUTTON_ID);
    private final By reloadButtonByXpath = By.xpath(RELOAD_BUTTON_XPATH);
    private final By warningImage = By.cssSelector(WARNING_IMAGE_CSS);
    private final By accessDeniedText = By.xpath(ACCESS_DENIED_TEXT_XPATH);

    private final WebDriver driver;

    public AccessDeniedPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickReloadButton() {
        try {
            log.debug("Пытаемся найти кнопку по id и нажать");
            driver.findElement(reloadButtonById).click();
            return;
        } catch (Exception e) {
            log.debug("Кнопка обновления страницы не найдена по id");
        }
        try {
            log.debug("Пытаемся найти кнопку по xpath и нажать");
            driver.findElement(reloadButtonByXpath).click();
            log.debug("Успешно нашли кнопку по xpath");
        } catch (Exception e) {
            log.debug("Кнопка обновления страницы не найдена по xpath");
        }
    }

    private boolean isWarningImage() {
        return driver.findElement(warningImage) != null;
    }

    private boolean isAccessDeniedText() {
        return driver.findElement(accessDeniedText) != null;
    }

    @Override
    public boolean isLoaded() {
        try {
            return isWarningImage() && isAccessDeniedText();
        } catch (Exception e) {
            return false;
        }
    }
}
