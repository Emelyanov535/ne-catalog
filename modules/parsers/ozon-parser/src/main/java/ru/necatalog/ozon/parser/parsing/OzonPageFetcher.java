package ru.necatalog.ozon.parser.parsing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.retry.annotation.Retryable;
import ru.necatalog.ozon.parser.parsing.pool.WebDriverPool;
import ru.necatalog.ozon.parser.parsing.pool.WebDriverPoolDestination;

@Slf4j
public class OzonPageFetcher {

    private final WebDriverPool webDriverPool;

    private final PageScroller pageScroller;

    public OzonPageFetcher(WebDriverPool webDriverPool,
                           PageScroller pageScroller) {
        this.webDriverPool = webDriverPool;
        this.pageScroller = pageScroller;
    }

    @Retryable
    public String fetchPageJson(String pageUrl,
                                Integer delay) throws InterruptedException {
        WebDriver driver = null;
        try {
            driver = webDriverPool.borrowDriver();
            if (!navigateWithTimeout(driver, pageUrl)) {
                driver = webDriverPool.recreate(driver);
                navigateWithTimeout(driver, pageUrl);
            }
            Thread.sleep(delay == null ? 100 : delay);
            //pageScroller.scrollPage(driver);
            String pageSource = driver.getPageSource();
            return pageSource;
        } finally {
            if (driver != null) {
                webDriverPool.returnDriver(driver);
            }
        }
    }

    private boolean navigateWithTimeout(WebDriver driver, String url) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<?> future = executor.submit(() -> driver.navigate().to(url));
            future.get(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
