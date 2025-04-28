package ru.necatalog.ozonparser.parser.pool;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.ObjectFactory;
import ru.necatalog.ozonparser.config.properties.OzonParserProperties;
import ru.necatalog.ozonparser.utils.OzonConsts;

@Slf4j
public class WebDriverPool {

    private final Queue<WebDriver> availableDrivers = new ConcurrentLinkedQueue<>();

    private final Queue<WebDriver> busyDrivers = new ConcurrentLinkedQueue<>();

    private final ObjectFactory<WebDriver> webDriverFactory;

    public WebDriverPool(ObjectFactory<WebDriver> webDriverFactory,
                         OzonParserProperties ozonConfigProperties) {
        this.webDriverFactory = webDriverFactory;
        int poolSize = ozonConfigProperties.getMaxThreads();

        for (int i = 0; i < poolSize; i++) {
            availableDrivers.add(createNewDriver());
        }
    }

    private WebDriver createNewDriver() {
        WebDriver wd = webDriverFactory.getObject();
        wd.manage().timeouts().pageLoadTimeout(Duration.of(10, ChronoUnit.SECONDS));
        return wd;
    }

    public WebDriver borrowDriver() {
        WebDriver driver = availableDrivers.poll();
        if (driver != null) {
            busyDrivers.add(driver);
            return driver;
        }
        throw new NoSuchElementException("No available driver found");
    }

    public void returnDriver(WebDriver driver) {
        busyDrivers.remove(driver);
        availableDrivers.add(driver);
    }

    @PreDestroy
    public void shutdownPool() {
        for (WebDriver driver : availableDrivers) {
            driver.quit();
        }

        for (WebDriver driver : busyDrivers) {
            driver.quit();
        }
        availableDrivers.clear();
        busyDrivers.clear();
    }

}
