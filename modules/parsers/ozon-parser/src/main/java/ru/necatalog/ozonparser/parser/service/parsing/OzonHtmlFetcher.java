package ru.necatalog.ozonparser.parser.service.parsing;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import ru.necatalog.ozonparser.parser.pool.WebDriverPool;
import ru.necatalog.ozonparser.parser.service.page.AccessDeniedPage;
import ru.necatalog.ozonparser.parser.service.page.CategoryPage;
import ru.necatalog.ozonparser.parser.service.page.NoContentPage;
import ru.necatalog.persistence.entity.ProductEntity;

@Slf4j
public class OzonHtmlFetcher {

    private final WebDriverPool webDriverPool;

    private final PageScroller pageScroller;

    public OzonHtmlFetcher(WebDriverPool webDriverPool,
                           PageScroller pageScroller) {
        this.webDriverPool = webDriverPool;
        this.pageScroller = pageScroller;
    }

    @Retryable(maxAttempts = 10, recover = "recover")
    public String fetchCategoryPageHtml(String pageUrl,
                                        AtomicBoolean lastPageInCategory) {
        var driver = webDriverPool.borrowDriver();
        try {
            log.info("Приступаем к обработке страницы {}", pageUrl);
            driver.get(pageUrl);

            Thread.sleep(100);
            WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
            var accessDeniedPage = new AccessDeniedPage(driver);
            var categoryPage = new CategoryPage(driver, wait);
            var noContentPage = new NoContentPage(driver, wait);
            wait.until(d -> checkForWaitingPageLoading(accessDeniedPage, categoryPage, noContentPage, lastPageInCategory));
            checkAceesDeniedAndResolve(accessDeniedPage);

            if (lastPageInCategory != null && !lastPageInCategory.get()) {
                pageScroller.scrollToEndOfPage(driver);
            }
            return driver.getPageSource();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            webDriverPool.returnDriver(driver);
        }
    }

    private boolean checkForWaitingPageLoading(AccessDeniedPage accessDeniedPage,
                                               CategoryPage categoryPage,
                                               NoContentPage noContentPage,
                                               AtomicBoolean stopFlag) {
        log.debug("Проверка что страница 'Доступ ограничен'");
        if (checkAccessDeniedPage(accessDeniedPage)) {
            return true;
        }
        log.debug("Проверка что страница 'Страница категории'");
        if (checkCategoryPage(categoryPage)) {
            return true;
        }
        if (checkNoContentPage(noContentPage) && stopFlag != null) {
            stopFlag.set(true);
            return true;
        }
        log.debug("Проверка загрузки страницы неудачна");
        return false;
    }

    private boolean checkCategoryPage(CategoryPage categoryPage) {
        return categoryPage.isLoaded();
    }

    private void checkAceesDeniedAndResolve(AccessDeniedPage accessDeniedPage) {
        if (checkAccessDeniedPage(accessDeniedPage)) {
            log.info("Доступ ограничен, пробуем решить проблему");
            resolveAccessDeniedPage(accessDeniedPage);
            log.info("Проблема успешно решена");
        }
    }

    private boolean checkNoContentPage(NoContentPage noContentPage) {
        if (noContentPage.isLoaded()) {
            log.info("Страница не найдена");
            return true;
        }
        return false;
    }

    private boolean checkAccessDeniedPage(AccessDeniedPage accessDeniedPage) {
        return accessDeniedPage.isLoaded();
    }

    private void resolveAccessDeniedPage(AccessDeniedPage accessDeniedPage) {
        accessDeniedPage.clickReloadButton();
    }

    @Recover
    private void recover(Exception e) {
        log.error("Все ретраи провалились");
    }

    public String fetchPageJson(ProductEntity product, String pageUrl) {
        WebDriver driver = null;
        try {
            driver = webDriverPool.borrowDriver();
            driver.navigate().to(pageUrl);
            Thread.sleep(100);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            return driver.getPageSource();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (driver != null) {
                webDriverPool.returnDriver(driver);
            }
        }
    }
}
