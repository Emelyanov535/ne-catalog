package ru.necatalog.ozon.parser.parsing;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebDriver;
import org.springframework.retry.annotation.Retryable;
import ru.necatalog.ozon.parser.parsing.dto.KeyValue;
import ru.necatalog.ozon.parser.parsing.pool.WebDriverPool;

@Slf4j
public class OzonPageFetcher {

    private final WebDriverPool webDriverPool;

    public OzonPageFetcher(WebDriverPool webDriverPool) {
        this.webDriverPool = webDriverPool;
    }

    @Retryable
    public String fetchPageJson(String pageUrl,
                                Integer delay) throws InterruptedException {
        KeyValue<Integer, WebDriver> driver = null;
        try {
            driver = webDriverPool.borrowDriver();
            if (!navigateWithTimeout(driver.getValue(), pageUrl)) {
                driver = webDriverPool.recreate(driver);
                navigateWithTimeout(driver.getValue(), pageUrl);
            }
            Thread.sleep(delay == null ? 100 : delay);
            return driver.getValue().getPageSource();
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (driver != null) {
                webDriverPool.returnDriver(driver);
            }
        }
    }

    private boolean navigateWithTimeout(WebDriver driver,
                                        String url) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<?> future = executor.submit(() -> driver.navigate().to(url));
            future.get(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
