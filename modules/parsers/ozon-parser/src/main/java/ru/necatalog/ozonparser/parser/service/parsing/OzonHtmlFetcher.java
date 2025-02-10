package ru.necatalog.ozonparser.parser.service.parsing;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import ru.necatalog.ozonparser.parser.pool.WebDriverPool;
import ru.necatalog.ozonparser.parser.service.page.AccessDeniedPage;
import ru.necatalog.ozonparser.parser.service.page.CategoryPage;
import ru.necatalog.ozonparser.parser.service.page.NoContentPage;

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
    public String fetchPageHtml(String pageUrl,
                                AtomicBoolean lastPageInCategory) {
        var driver = webDriverPool.borrowDriver();
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.of(10, ChronoUnit.SECONDS));
            driver.get(pageUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, ChronoUnit.SECONDS));
            var accessDeniedPage = new AccessDeniedPage(driver, wait);
            var categoryPage = new CategoryPage(driver, wait);
            var noContentPage = new NoContentPage(driver, wait);
            wait.until(d -> checkForWaitingPageLoading(accessDeniedPage, categoryPage, noContentPage, lastPageInCategory));
            checkAceesDeniedAndResolve(accessDeniedPage);

            pageScroller.scrollToEndOfPage(driver);
            return driver.getPageSource();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
        if (checkNoContentPage(noContentPage)) {
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

}
